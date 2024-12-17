package org.finra.rmcs.constants;

import java.util.Arrays;

public enum BuCodeEnum {
  WALLET("WALLET","WALLET"),
  CATFB("CATFB","CAT FB"),
  CRDRN("CRDRN","Central Registration Depository Renewal"),
  RGFEE("RGFEE","Regulatory Transaction Fee"),
  REGT("REGT","Regulation T"),
  CRDRG("CRDRG","Central Registration Depository"),
  ADFBI("ADFBI","Miscellaneous Billing"),
  ADFRF("ADFRF","Alternative Display Facility"),
  ADVRG("ADVRG","Communication Regulation Review"),
  EDUCA("EDUCA","Education and Training"),
  GASBE("GASBE","GASB Accounting Support Fee"),
  IARCE("IARCE","IAR Continuing Education"),
  MREGN("MREGN","Member Regulatory Fees"),
  MTRCS("MTRCS","Dispute Resolution"),
  ORFBI("ORFBI","OTC Reporting Facility"),
  TAFBI("TAFBI","Trading Activity Fee"),
  TRACE("TRACE","Trade Reporting and Compliance Engine"),
  APIBI("APIBI","API Developer Center"),
  CMABI("CMABI","Continuing Membership Application"),
  FREGN("FREGN","Funding Portal Member Fees");

  private final String value;
  private final String description;

  BuCodeEnum(String value,String description)
  {
    this.value = value;
    this.description = description;
  }

  public static String findByStringValue(String type) {
    if (type == null) {
      return null;
    }
    return Arrays.stream(BuCodeEnum.values())
        .filter(it -> it.name().contains(type))
        .findFirst()
        .map(BuCodeEnum::getValue)
        .orElse(null);
  }

  public String getDescription() {
   return description;
  }

  public String getValue() {
    return value;
  }
}
