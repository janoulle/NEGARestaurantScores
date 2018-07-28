package com.janeullah.healthinspectionrecords.async;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Streams;
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
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.janeullah.healthinspectionrecords.constants.WebSelectorConstants.*;

@Slf4j
class RestaurantProcessor {
    private static final Pattern LEADING_DIGIT_MATCHER = Pattern.compile("^[0-9]+");
    // 10 represents the count of chars in a date e.g. mm/dd/yyyy has exactly 10 chars
    private static final int DATE_CHAR_COUNT = 10;
    private String countyName;
    private Element rowElement;
    private Elements hiddenDivs;

    /**
     * @param county     string representing the county name
     * @param rowElement Element representing a restaurant entry on
     *                   http://publichealthathens.com/wp/programs/restaurantinspections/
     * @param hiddenDivs Elements representing the hidden div data that holds additional details on
     *                   critical violations
     */
    RestaurantProcessor(String county, Element rowElement, Elements hiddenDivs) {
        this.countyName = county;
        this.rowElement = rowElement;
        this.hiddenDivs = hiddenDivs;
    }

    private Optional<EstablishmentInfo> createEstablishmentInfo() {
        Element address = rowElement.select(RESTAURANT_ADDRESS_SELECTOR).first();
        Element name = rowElement.select(RESTAURANT_NAME_SELECTOR).first();
        EstablishmentInfo info = new EstablishmentInfo();
        if (name != null && StringUtils.isNotBlank(name.text())) {
            info.setName(name.text().trim());
        }
        if (address != null && StringUtils.isNotBlank(address.text())) {
            info.setAddress(address.text().trim());
        }
        return info.getName() != null ? Optional.of(info) : Optional.empty();
    }

    /**
     * http://www.joda.org/joda-time/key_format.html
     *
     * @return JodaTime DateTime object representing the date the inspection was conducted
     */
    private Optional<LocalDate> createDateOfMostRecentInspection() {
        Element when = rowElement.select(DATE_SELECTOR).first();
        try {
            if (when != null && StringUtils.isNotBlank(when.text())) {
                if (when.text().length() > DATE_CHAR_COUNT) {
                    String[] splitTimes = when.text().split(" / ");
                    return Stream.of(splitTimes)
                            .filter(Objects::nonNull)
                            .map(entry -> LocalDate.parse(entry, InspectionReport.MMddYYYY_PATTERN))
                            .max(LocalDate::compareTo);
                }
                return Optional.of(LocalDate.parse(when.text(), InspectionReport.MMddYYYY_PATTERN));
            }
        } catch (PatternSyntaxException | DateTimeParseException e) {
            log.error(
                    "Exception while extracting and parsing the most recent date of an inspection from rowElement {}",
                    rowElement.toString(),
                    e);
        }
        return Optional.empty();
    }

    private Optional<InspectionType> createInspectionType() {
        Element type = rowElement.select(INSPECTION_TYPE_SELECTOR).first();
        String text = type != null ? StringUtils.trimToEmpty(type.text()) : "";
        // The convention appears to be that 're-inspections' have happened by appending the newer date
        // to the preceding inspection date.

        if (text.contains("/")) {
            return Optional.of(InspectionType.RE_INSPECTION);
        }
        return InspectionType.asInspectionType(text);
    }

    /**
     * Return the numeric score and defaults to 0 if an error occurs
     * http://www.baeldung.com/guava-string-charmatcher
     *
     * @return score as int
     */
    private int createInspectionScore() {
        Elements potentialScore = rowElement.select(SCORE_SELECTOR);
        if (potentialScore != null && potentialScore.first() != null) {
            Optional<Matcher> lastDigits =
                    splitParameterOnWhitespace(potentialScore.first().text())
                            .map(LEADING_DIGIT_MATCHER::matcher)
                            .filter(Matcher::lookingAt)
                            .reduce((a, b) -> b);

            if (lastDigits.isPresent()) {
                String matchedVal = StringUtils.trimToEmpty(lastDigits.get().group());
                // https://stackoverflow.com/questions/3265948/nullpointerexception-with-autoboxing-in-ternary-expression
                Integer scoreVal =
                        StringUtils.isNumeric(matchedVal) ? Ints.tryParse(matchedVal) : new Integer(0);
                return scoreVal != null ? scoreVal : 0;
            }
        }

        return 0;
    }

    private Stream<String> splitParameterOnWhitespace(String value) {
        return Streams.stream(Splitter.on(CharMatcher.whitespace())
                .trimResults()
                .omitEmptyStrings()
                .split(StringUtils.trimToEmpty(value)));
    }

    private List<Violation> createViolations() {
        List<Violation> violations = new ArrayList<>();
        violations.addAll(extractViolations(CRITICAL, Severity.CRITICAL));
        violations.addAll(extractViolations(NOT_CRITICAL, Severity.NONCRITICAL));
        return violations;
    }

    private List<Violation> extractViolations(String selector, Severity severity) {
        return rowElement
                .select(selector)
                .stream()
                .filter(entry -> StringUtils.isNotBlank(entry.id()))
                .map(entry -> extractViolationPOJO(entry.id(), severity, entry))
                .collect(Collectors.toList());
    }

    private Violation extractViolationPOJO(
            String hrefId, Severity severity, Element violationElement) {
        Violation v = new Violation();
        v.setSeverity(severity);
        v.setSummary(violationElement.text());
        v.setCategory(extractCategoryFromSummary(v.getSummary()));
        String violationHrefId;
        String hiddenText = "";
        if (hasViolationHrefId(hrefId, severity)) {
            violationHrefId = hrefId.substring(HREF_PREFIX_FOR_VIOLATIONS.length());
            hiddenText = extractHiddenTextForViolation(violationHrefId);
        }

        v.setNotes(hiddenText);
        return v;
    }

    private boolean hasViolationHrefId(String hrefId, Severity severity) {
        return StringUtils.isNotBlank(hrefId)
                && (severity == Severity.NONCRITICAL || severity == Severity.CRITICAL)
                && hrefId.length() > HREF_PREFIX_FOR_VIOLATIONS.length();
    }

    private String extractCategoryFromSummary(String violationSummary) {
        if (hasViolationSummary(violationSummary)) {
            int startingIndex =
                    violationSummary.indexOf(WebPageConstants.VIOLATION_CODE_PREFIX)
                            + WebPageConstants.VIOLATION_CODE_PREFIX.length();
            int suffixIndex = violationSummary.indexOf(WebPageConstants.VIOLATION_CODE_SUFFIX);
            if (suffixIndex > startingIndex) {
                String code = violationSummary.substring(startingIndex, suffixIndex);
                return StringUtils.trimToEmpty(code);
            }
        }
        return StringUtils.EMPTY;
    }

    // Web page convention is to precede violations with the 'Violation of :' text
    private boolean hasViolationSummary(String violationSummary) {
        return StringUtils.isNotEmpty(violationSummary)
                && violationSummary.length()
                > (WebPageConstants.VIOLATION_CODE_PREFIX.length()
                + WebPageConstants.VIOLATION_CODE_SUFFIX.length());
    }

    private String extractHiddenTextForViolation(String idForViolation) {
        if (hiddenDivs != null) {
            Elements selection = hiddenDivs.select("div#" + idForViolation);
            return selection != null && selection.first() != null
                    ? StringUtils.trimToEmpty(selection.first().text())
                    : "";
        }
        return StringUtils.EMPTY;
    }

    private InspectionReport createInspectionReport() {

        InspectionReport report = new InspectionReport();
        report.setScore(createInspectionScore());
        report.addViolations(createViolations());
        createDateOfMostRecentInspection().ifPresent(report::setDateReported);
        createInspectionType().ifPresent(report::setInspectionType);
        return report;
    }

    Optional<Restaurant> createRestaurant() {
        try {
            Optional<EstablishmentInfo> info = createEstablishmentInfo();
            info.ifPresent(entry -> entry.setCounty(countyName));
            if (info.isPresent()) {

                Restaurant restaurant = new Restaurant();
                restaurant.setEstablishmentInfo(info.get());
                restaurant.addInspectionReport(createInspectionReport());

                return Optional.of(restaurant);
            }
        } catch (Selector.SelectorParseException e) {
            log.error(
                    "JSoup parse exception while processing rowElement {} for county {}",
                    countyName,
                    rowElement.toString(),
                    e);
        }
        return Optional.empty();
    }

}