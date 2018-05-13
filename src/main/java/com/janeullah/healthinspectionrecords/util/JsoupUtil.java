package com.janeullah.healthinspectionrecords.util;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.primitives.Ints;
import com.janeullah.healthinspectionrecords.constants.InspectionType;
import com.janeullah.healthinspectionrecords.constants.Severity;
import com.janeullah.healthinspectionrecords.constants.WebPageConstants;
import com.janeullah.healthinspectionrecords.domain.entities.EstablishmentInfo;
import com.janeullah.healthinspectionrecords.domain.entities.InspectionReport;
import com.janeullah.healthinspectionrecords.domain.entities.Restaurant;
import com.janeullah.healthinspectionrecords.domain.entities.Violation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.janeullah.healthinspectionrecords.constants.WebSelectorConstants.*;

/** Author: Jane Ullah Date: 9/18/2016 */
@Slf4j
public class JsoupUtil {
  private static final Pattern LEADING_DIGIT_MATCHER = Pattern.compile("^[0-9]+");
  private static final String DIV = "div#";
  private static final String FWD_SLASH = " / ";
  // 10 represents the count of chars in a date e.g. mm/dd/yyyy has exactly 10 chars
  private static final int DATE_CHAR_COUNT = 10;

  private JsoupUtil() {}

  private static EstablishmentInfo extractEstablishmentInfoFromElement(Element rowElement) {
    Element address = rowElement.select(RESTAURANT_ADDRESS_SELECTOR).first();
    Element name = rowElement.select(RESTAURANT_NAME_SELECTOR).first();
    EstablishmentInfo info = new EstablishmentInfo();
    if (name != null && StringUtils.isNotBlank(name.text())) {
      info.setName(name.text().trim());
    }
    if (address != null && StringUtils.isNotBlank(address.text())) {
      info.setAddress(address.text().trim());
    }
    return info;
  }

  private static Optional<InspectionType> extractInspectionTypeFromElement(Element rowElement) {
    Element type = rowElement.select(INSPECTION_TYPE_SELECTOR).first();
    try {
      if (type != null && StringUtils.isNotBlank(type.text())) {
        // TODO: verify this logic
        if (type.text().contains(StringUtilities.FORWARD_SLASH.getValue())) {
          return Optional.of(InspectionType.RE_INSPECTION);
        }
        return Optional.of(InspectionType.asInspectionType(type.text().trim()));
      }
    } catch (Exception e) {
      log.error(
          "Exception while extracting inspection type from Element {}", rowElement.toString(), e);
    }
    return Optional.empty();
  }

  /**
   * Return the numeric score and defaults to 0 if an error occurs
   * http://www.baeldung.com/guava-string-charmatcher
   *
   * @param rowElement Represents a row in the tabular doc
   * @return score as int
   */
  private static int extractScoreFromElement(Element rowElement) {
    Elements potentialScore = rowElement.select(SCORE_SELECTOR);
    try {
      if (potentialScore != null) {
        List<String> splits = new ArrayList<>();
        Splitter.on(CharMatcher.WHITESPACE)
            .trimResults()
            .omitEmptyStrings()
            .split(potentialScore.first().text())
            .forEach(splits::add);

        String lastDigitMatched = "";
        for (String split : splits) {
          Matcher matcher = LEADING_DIGIT_MATCHER.matcher(split);
          if (matcher.lookingAt()) {
            lastDigitMatched = matcher.group();
          }
        }

        if (StringUtils.isNumeric(lastDigitMatched)) {
          Integer scoreVal = Ints.tryParse(lastDigitMatched);
          return scoreVal != null ? scoreVal : 0;
        }
      }
    } catch (IllegalArgumentException e) {
      log.error("Exception extracting score from rowElement {}", rowElement.toString(), e);
    }
    return 0;
  }

  /**
   * http://www.joda.org/joda-time/key_format.html
   *
   * @param rowElement Represents a row in the tabular doc
   * @return JodaTime DateTime object representing the date the inspection was conducted
   */
  private static Optional<LocalDate> extractMostRecentDateFromElement(Element rowElement) {
    Element when = rowElement.select(DATE_SELECTOR).first();
    try {
      if (when != null && StringUtils.isNotBlank(when.text())) {
        if (when.text().length() > DATE_CHAR_COUNT) {
          String[] splitTimes = when.text().split(FWD_SLASH);
          return Stream.of(splitTimes)
              .map(entry -> LocalDate.parse(entry, InspectionReport.MMddYYYY_PATTERN))
              .max(LocalDate::compareTo);
        }
        return Optional.of(LocalDate.parse(when.text(), InspectionReport.MMddYYYY_PATTERN));
      }
    } catch (UnsupportedOperationException | IllegalArgumentException e) {
      log.error(
          "Exception while extracting and parsing the most recent date of an inspection from rowElement {}",
          rowElement.toString(),
          e);
    }
    return Optional.empty();
  }

  private static List<Violation> extractViolations(Element cellElement, Elements hiddenDiv) {
    List<Violation> violations = new ArrayList<>();
    violations.addAll(
        extractViolations(
            CRITICAL, Severity.CRITICAL, cellElement, hiddenDiv));
    violations.addAll(
        extractViolations(
            NOT_CRITICAL, Severity.NONCRITICAL, cellElement, hiddenDiv));
    return violations;
  }

  private static List<Violation> extractViolations(
      String selector, Severity severity, Element cellElement, Elements hiddenDivs) {
    return cellElement
        .select(selector)
        .stream()
        .filter(entry -> StringUtils.isNotBlank(entry.id()))
        .map(entry -> extractViolationPOJO(entry.id(), severity, entry, hiddenDivs))
        .collect(Collectors.toList());
  }

  private static Violation extractViolationPOJO(
      String hrefId, Severity severity, Element element, Elements hiddenDivs) {
    Violation v = new Violation();
    v.setSeverity(severity);
    v.setSummary(element.text());
    v.setCategory(extractCategoryFromSummary(v.getSummary()));
    String violationHrefId;
    String hiddenText = "";
    if (hasViolationHrefId(hrefId, severity)) {
      violationHrefId = hrefId.substring(HREF_PREFIX_FOR_VIOLATIONS.length());
      hiddenText = extractHiddenTextForViolation(violationHrefId, hiddenDivs);
    }

    v.setNotes(hiddenText);
    return v;
  }

  private static boolean hasViolationHrefId(String hrefId, Severity severity) {
    return StringUtils.isNotBlank(hrefId)
        && (severity == Severity.NONCRITICAL || severity == Severity.CRITICAL)
        && hrefId.length() > HREF_PREFIX_FOR_VIOLATIONS.length();
  }

  private static String extractCategoryFromSummary(String violationSummary) {
    if (hasViolationSummary(violationSummary)) {
      int startingIndex =
          violationSummary.indexOf(WebPageConstants.VIOLATION_CODE_PREFIX)
              + WebPageConstants.VIOLATION_CODE_PREFIX.length();
      int suffixIndex = violationSummary.indexOf(WebPageConstants.VIOLATION_CODE_SUFFIX);
      if (suffixIndex > startingIndex) {
        String code = violationSummary.substring(startingIndex, suffixIndex);
        return StringUtils.isNotBlank(code) ? code.trim() : StringUtils.EMPTY;
      }
    }
    return StringUtils.EMPTY;
  }

  private static boolean hasViolationSummary(String violationSummary) {
    return StringUtils.isNotEmpty(violationSummary)
        && violationSummary.length()
            > (WebPageConstants.VIOLATION_CODE_PREFIX.length()
                + WebPageConstants.VIOLATION_CODE_SUFFIX.length());
  }

  private static String extractHiddenTextForViolation(String idForViolation, Elements hiddenDivs) {
    if (hiddenDivs != null) {
      Element hiddenDiv = hiddenDivs.select(DIV + idForViolation).first();
      return hiddenDiv != null ? hiddenDiv.text() : StringUtils.EMPTY;
    }
    return StringUtils.EMPTY;
  }

  public static Optional<Restaurant> assemblePOJO(String county, Element rowElement, Elements hiddenDivs) {
    try {
      int score = extractScoreFromElement(rowElement);
      Optional<LocalDate> dateReported = extractMostRecentDateFromElement(rowElement);
      Optional<InspectionType> typeOfInspection = extractInspectionTypeFromElement(rowElement);
      List<Violation> violations = extractViolations(rowElement, hiddenDivs);

      InspectionReport report = new InspectionReport();
      report.setScore(score);
      report.addViolations(violations);
      dateReported.ifPresent(report::setDateReported);
      typeOfInspection.ifPresent(report::setInspectionType);

      Restaurant restaurant = new Restaurant();
      EstablishmentInfo info = extractEstablishmentInfoFromElement(rowElement);
      info.setCounty(county);
      restaurant.setEstablishmentInfo(info);
      restaurant.addInspectionReport(report);
      return Optional.of(restaurant);
    } catch (Selector.SelectorParseException e) {
      log.error(
          "Exception while putting together Restaurant POJO from rowElement {}",
          rowElement.toString(),
          e);
    }
    return Optional.empty();
  }
}
