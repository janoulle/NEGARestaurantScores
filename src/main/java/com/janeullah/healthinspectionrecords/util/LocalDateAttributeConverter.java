package com.janeullah.healthinspectionrecords.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Date;
import java.time.LocalDate;

/**
 * Ahttp://www.thoughts-on-java.org/persist-localdate-localdatetime-jpa/
 * Date:  9/20/2016
 */
@Converter(autoApply = true)
public class LocalDateAttributeConverter implements AttributeConverter<LocalDate, Date> {

    private static final Logger logger = LoggerFactory.getLogger(LocalDateAttributeConverter.class);

    @Override
    public Date convertToDatabaseColumn(LocalDate attribute) {
        if (attribute != null) {
            logger.info("converting localdate to sqldate");
            return Date.valueOf(attribute);
        }
        return null;
    }

    @Override
    public LocalDate convertToEntityAttribute(Date sqlDate) {
        logger.info("converting sqldate to localdate");
        return sqlDate != null ? sqlDate.toLocalDate() : null;
    }
}