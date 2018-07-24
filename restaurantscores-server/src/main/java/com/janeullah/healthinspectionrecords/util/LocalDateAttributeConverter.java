package com.janeullah.healthinspectionrecords.util;

import lombok.extern.slf4j.Slf4j;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Date;
import java.time.LocalDate;

/** Ahttp://www.thoughts-on-java.org/persist-localdate-localdatetime-jpa/ Date: 9/20/2016 */
@Slf4j
@Converter(autoApply = true)
public class LocalDateAttributeConverter implements AttributeConverter<LocalDate, Date> {

  @Override
  public Date convertToDatabaseColumn(LocalDate attribute) {
    if (attribute != null) {
      log.info("converting localdate to sqldate");
      return Date.valueOf(attribute);
    }
    return null;
  }

  @Override
  public LocalDate convertToEntityAttribute(Date sqlDate) {
    log.info("converting sqldate to localdate");
    return sqlDate != null ? sqlDate.toLocalDate() : null;
  }
}
