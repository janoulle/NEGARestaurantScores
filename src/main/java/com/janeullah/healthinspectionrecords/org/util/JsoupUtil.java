package com.janeullah.healthinspectionrecords.org.util;

import com.google.common.primitives.Ints;
import com.janeullah.healthinspectionrecords.org.constants.InspectionType;
import com.janeullah.healthinspectionrecords.org.constants.Severity;
import com.janeullah.healthinspectionrecords.org.constants.WebSelectorConstants;
import com.janeullah.healthinspectionrecords.org.model.EstablishmentInfo;
import com.janeullah.healthinspectionrecords.org.model.InspectionReport;
import com.janeullah.healthinspectionrecords.org.model.Restaurant;
import com.janeullah.healthinspectionrecords.org.model.Violation;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Author: jane
 * Date:  9/18/2016
 */
public class JsoupUtil {
    private final static Logger logger = Logger.getLogger(JsoupUtil.class);


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
            logger.error(e);
        }
        return null;
    }

    /**
     * Return the numeric score
     *
     * @param rowElement Represents a row in the tabular doc
     * @return score as int
     * @throws Selector.SelectorParseException selector used is invalid
     */
    private static int extractScoreFromElement(Element rowElement) throws Selector.SelectorParseException {
        Element score = rowElement.select(WebSelectorConstants.SCORE_SELECTOR).first();
        try {
            if (score != null && StringUtils.isNumeric(score.text())) {
                Object scoreVal = Ints.tryParse(score.text());
                return scoreVal != null ? (int) scoreVal : 0;
            }
        } catch (IllegalArgumentException e) {
            logger.error(e);
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
    private static DateTime extractMostRecentDateFromElement(Element rowElement) throws Selector.SelectorParseException {
        Element when = rowElement.select(WebSelectorConstants.DATE_SELECTOR).first();
        try {
            if (when != null && StringUtils.isNotBlank(when.text())) {
                DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yyyy");
                if (when.text().length() > 10) {
                    String[] splitTimes = when.text().split(" / ");
                    Optional<DateTime> tentativeResult = Stream.of(splitTimes)
                            .map(entry -> DateTime.parse(entry, fmt))
                            .max(DateTime::compareTo);
                    if (tentativeResult.isPresent()) {
                        return tentativeResult.get();
                    }
                } else {
                    return DateTime.parse(when.text(), fmt);
                }
            }
        } catch (UnsupportedOperationException | IllegalArgumentException e) {
            logger.error(e);
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
                .map(entry -> extractViolationPOJO(entry.id(), severity, entry, hiddenDivs))
                .collect(Collectors.toList());
    }

    private static Violation extractViolationPOJO(String hrefId, Severity severity, Element element, Elements hiddenDivs) {
        Violation v = new Violation();
        v.setSeverity(severity);
        v.setSummary(element.text());
        String violationHrefId = "";
        switch (severity) {
            case NONCRITICAL:
                violationHrefId = hrefId.substring(WebSelectorConstants.NON_CRITICAL_HREF_ID_PREFIX.length() - 1);
                break;
            case CRITICAL:
                violationHrefId = hrefId.substring(WebSelectorConstants.CRITICAL_HREF_ID_PREFIX.length() - 1);
                break;
        }
        String hiddenText = extractHiddenTextForViolation(violationHrefId, hiddenDivs);
        v.setNotes(hiddenText);
        return v;
    }

    private static String extractHiddenTextForViolation(String idForViolation, Elements hiddenDivs) {
        if (hiddenDivs != null) {
            Element hiddenDiv = hiddenDivs.select("div#" + idForViolation).first();
            return hiddenDiv != null ? hiddenDiv.text() : StringUtils.EMPTY;
        }
        return StringUtils.EMPTY;
    }

    public static Restaurant assemblePOJO(Element rowElement, Elements hiddenDivs) {
        try {
            EstablishmentInfo info = extractEstablishmentInfoFromElement(rowElement);
            int score = extractScoreFromElement(rowElement);
            DateTime when = extractMostRecentDateFromElement(rowElement);
            InspectionType typeOfInspection = extractInspectionTypeFromElement(rowElement);
            List<Violation> violations = extractViolations(rowElement, hiddenDivs);

            InspectionReport report = new InspectionReport();
            report.setScore(score);
            report.setDateReported(when);
            report.setInspectionType(typeOfInspection);
            report.setViolations(violations);

            Restaurant restaurant = new Restaurant();
            restaurant.setEstablishmentInfo(info);
            restaurant.addInspectionReport(report);
            return restaurant;
        } catch (Exception e) {
            logger.error(e);
        }
        return null;
    }
}
