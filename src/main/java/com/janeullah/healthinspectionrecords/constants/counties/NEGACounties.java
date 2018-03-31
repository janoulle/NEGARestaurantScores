package com.janeullah.healthinspectionrecords.constants.counties;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Author: Jane Ullah Date: 3/26/2017 */
public enum NEGACounties {
  CLARKE("Clarke"),
  ELBERT("Elbert"),
  BARROW("Barrow"),
  MADISON("Madison"),
  JACKSON("Jackson"),
  OCONEE("Oconee"),
  GREENE("Greene"),
  WALTON("Walton"),
  OGLETHORPE("Oglethorpe"),
  MORGAN("Morgan");

  String normalizedName;

  NEGACounties(String normalizedName) {
    this.normalizedName = normalizedName;
  }

  public static List<String> getAllNEGACounties() {
    return Stream.of(NEGACounties.values())
        .map(NEGACounties::getNormalizedName)
        .collect(Collectors.toList());
  }

  public String getNormalizedName() {
    return normalizedName;
  }
}
