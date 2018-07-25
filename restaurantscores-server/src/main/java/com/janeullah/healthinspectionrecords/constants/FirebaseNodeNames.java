package com.janeullah.healthinspectionrecords.constants;

/** Author: Jane Ullah Date: 7/8/2017 */
public enum FirebaseNodeNames {
  COUNTIES("counties"),
  RESTAURANTS("restaurants"),
  VIOLATIONS("violations");

  String nodeName;

  FirebaseNodeNames(String val) {
    this.nodeName = val;
  }

  public String getNodeName() {
    return nodeName;
  }
}
