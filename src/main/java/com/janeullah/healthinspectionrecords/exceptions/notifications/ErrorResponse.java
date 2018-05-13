package com.janeullah.healthinspectionrecords.exceptions.notifications;

import lombok.AllArgsConstructor;
import lombok.Data;

/** Author: jane Date: 4/28/2017 */
@AllArgsConstructor
@Data
public class ErrorResponse {
  private String code;
  private String message;
  private Throwable throwable;

}
