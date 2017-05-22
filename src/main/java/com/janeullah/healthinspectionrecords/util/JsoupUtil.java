package com.janeullah.healthinspectionrecords.util;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.primitives.Ints;
import com.janeullah.healthinspectionrecords.constants.InspectionType;
import com.janeullah.healthinspectionrecords.constants.Severity;
import com.janeullah.healthinspectionrecords.constants.WebPageConstants;
import com.janeullah.healthinspectionrecords.constants.WebSelectorConstants;
import com.janeullah.healthinspectionrecords.domain.entities.EstablishmentInfo;
import com.janeullah.healthinspectionrecords.domain.entities.InspectionReport;
import com.janeullah.healthinspectionrecords.domain.entities.Restaurant;
import com.janeullah.healthinspectionrecords.domain.entities.Violation;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Author: Jane Ullah
 * Date:  9/18/2016
 */
public class JsoupUtil {
    private final static Logger logger = LoggerFactory.getLogger(JsoupUtil.class);
    private static final Pattern leadingDigitMatcher = Pattern.compile("^[0-9]+");

    private static EstablishmentInfo extractEstablishmentInfoFromElement(Element rowElement) throws Selector.SelectorParseException {
        Element address = rowElement.select(WebSelectorConstants.RESTAURANT_ADDRESS_SELECTOR).first();
        Element name = rowElement.select(WebSelectorConstants.RESTAURANT_NAME_SELECTOR).first();
        EstablishmentInfo info = new EstablishmentInfo();
        if (name != null && StringUtils.isNotBlank(name.text())) {
            info.setName(name.text().trim());
        }
        if (address != null && StringUtils.isNotBlank(address.text())) {
            info.setAddress(address.text().trim());
        }
        return info;
    }

    private static InspectionType extractInspectionTypeFromElement(Element rowElement) throws Selector.SelectorParseException {
        Element type = rowElement.select(WebSelectorConstants.INSPECTION_TYPE_SELECTOR).first();
        try {
            if (type != null && StringUtils.isNotBlank(type.text())) {
                //TODO: verify this logic
                if (type.text().contains("/")) {
                    return InspectionType.RE_INSPECTION;
                }
                return InspectionType.asInspectionType(type.text().trim());
            }
        } catch (Exception e) {
            logger.error("Exception while extracting inspection type from Element {}",rowElement.toString(),e);
        }
        return null;
    }

    /**
     * Return the numeric score and defaults to 0 if an error occurs
     * http://www.baeldung.com/guava-string-charmatcher
     *
     * @param rowElement Represents a row in the tabular doc
     * @return score as int
     * @throws Selector.SelectorParseException selector used is invalid
     */
    private static int extractScoreFromElement(Element rowElement) throws Selector.SelectorParseException {
        Elements potentialScore = rowElement.select(WebSelectorConstants.SCORE_SELECTOR);
        try {
            if (potentialScore != null){
                List<String> splits = new ArrayList<>();
                Splitter.on(CharMatcher.WHITESPACE)
                        .trimResults()
                        .omitEmptyStrings()
                        .split(potentialScore.first().text())
                        .forEach(splits::add);

                String lastDigitMatched = "";
                for (String split : splits) {
                    Matcher matcher = leadingDigitMatcher.matcher(split);
                    if (matcher.lookingAt()) {
                        lastDigitMatched = matcher.group();
                    }
                }

                if  (StringUtils.isNumeric(lastDigitMatched)) {
                    Integer scoreVal = Ints.tryParse(lastDigitMatched);
                    return scoreVal != null ? scoreVal : 0;
                }
            }
        } catch (IllegalArgumentException e) {
            logger.error("Exception extracting score from rowElement {}",rowElement.toString(),e);
        }
        return 0;
    }

    /**
     * http://www.joda.org/joda-time/key_format.html
     *
     * @param rowElement Represents a row in the tabular doc
     * @return JodaTime DateTime object representing the date the inspection was conducted
     * @throws Selector.SelectorParseException parsing exception from Jsoup
     */
    private static LocalDate extractMostRecentDateFromElement(Element rowElement) throws Selector.SelectorParseException {
        Element when = rowElement.select(WebSelectorConstants.DATE_SELECTOR).first();
        try {
            if (when != null && StringUtils.isNotBlank(when.text())) {
                //TODO: remove this magic number. 10 represents the count of chars in a date e.g. mm/dd/yyyy has exactly 10 chars
                if (when.text().length() > 10) {
                    String[] splitTimes = when.text().split(" / ");
                    Optional<LocalDate> tentativeResult = Stream.of(splitTimes)
                            .map(entry -> LocalDate.parse(entry, InspectionReport.MMddYYYY_PATTERN))
                            .max(LocalDate::compareTo);
                    return tentativeResult.orElse(null);
                }
                return LocalDate.parse(when.text(), InspectionReport.MMddYYYY_PATTERN);
            }
        } catch (UnsupportedOperationException | IllegalArgumentException e) {
            logger.error("Exception while extracting and parsing the most recent date of an inspection from rowElement {}",rowElement.toString(),e);
        }
        return null;
    }

    private static List<Violation> extractViolations(Element cellElement, Elements hiddenDiv) {
        List<Violation> violations = new ArrayList<>();
        violations.addAll(extractViolations(WebSelectorConstants.CRITICAL, Severity.CRITICAL, cellElement, hiddenDiv));
        violations.addAll(extractViolations(WebSelectorConstants.NOT_CRITICAL, Severity.NONCRITICAL, cellElement, hiddenDiv));
        return violations;
    }

    private static List<Violation> extractViolations(String selector, Severity severity, Element cellElement, Elements hiddenDivs) {
        return cellElement.select(selector)
                .stream()
                .filter(entry -> StringUtils.isNotBlank(entry.id()))
                .map(entry -> extractViolationPOJO(entry.id(), severity, entry, hiddenDivs))
                .collect(Collectors.toList());
    }

    private static Violation extractViolationPOJO(String hrefId, Severity severity, Element element, Elements hiddenDivs) {
        Violation v = new Violation();
        v.setSeverity(severity);
        v.setSummary(element.text());
        v.setCategory(extractCategoryFromSummary(v.getSummary()));
        String violationHrefId;
        String hiddenText = "";
        if (StringUtils.isNotBlank(hrefId)) {
            switch (severity) {
                case NONCRITICAL:
                case CRITICAL:
                    if (hrefId.length() > WebSelectorConstants.HREF_PREFIX_FOR_VIOLATIONS.length()) {
                        violationHrefId = hrefId.substring(WebSelectorConstants.HREF_PREFIX_FOR_VIOLATIONS.length());
                        hiddenText = extractHiddenTextForViolation(violationHrefId, hiddenDivs);
                    }
                    break;
            }
        }
        v.setNotes(hiddenText);
        return v;
    }

    private static String extractCategoryFromSummary(String violationSummary) {
        if (StringUtils.isNotEmpty(violationSummary)) {
            if (violationSummary.length() > (WebPageConstants.VIOLATION_CODE_PREFIX.length() + WebPageConstants.VIOLATION_CODE_SUFFIX.length())) {
                int startingIndex = violationSummary.indexOf(WebPageConstants.VIOLATION_CODE_PREFIX) + WebPageConstants.VIOLATION_CODE_PREFIX.length();
                int suffixIndex = violationSummary.indexOf(WebPageConstants.VIOLATION_CODE_SUFFIX);
                if (suffixIndex > startingIndex) {
                    String code = violationSummary.substring(startingIndex,suffixIndex);
                    return StringUtils.isNotBlank(code) ? code.trim() : StringUtils.EMPTY;
                }
            }
        }
        return StringUtils.EMPTY;
    }

    private static String extractHiddenTextForViolation(String idForViolation, Elements hiddenDivs) {
        if (hiddenDivs != null) {
            Element hiddenDiv = hiddenDivs.select("div#" + idForViolation).first();
            return hiddenDiv != null ? hiddenDiv.text() : StringUtils.EMPTY;
        }
        return StringUtils.EMPTY;
    }

    public static Optional<Restaurant> assemblePOJO(Element rowElement, Elements hiddenDivs) {
        try {
            EstablishmentInfo info = extractEstablishmentInfoFromElement(rowElement);
            int score = extractScoreFromElement(rowElement);
            LocalDate when = extractMostRecentDateFromElement(rowElement);
            InspectionType typeOfInspection = extractInspectionTypeFromElement(rowElement);
            List<Violation> violations = extractViolations(rowElement, hiddenDivs);

            InspectionReport report = new InspectionReport();
            report.setScore(score);
            report.setDateReported(when);
            report.setInspectionType(typeOfInspection);
            report.addViolations(violations);

            Restaurant restaurant = new Restaurant();
            restaurant.setEstablishmentInfo(info);
            restaurant.addInspectionReport(report);
            return Optional.of(restaurant);
        } catch (Exception e) {
            logger.error("Exception while putting together Restaurant POJO from rowElement {}",rowElement.toString(),e);
        }
        return Optional.empty();
    }
}
