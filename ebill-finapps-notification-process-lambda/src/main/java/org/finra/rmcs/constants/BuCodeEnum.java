package org.finra.rmcs.constants;

import java.util.Arrays;

public enum BuCodeEnum {
  CRDRG("CRDRG", "CRG"),
  MREGN("MREGN", "MRG"),
  TRACE("TRACE", "TRC"),
  MTRCS("MTRCS", "DRF"),
  ADVRG("ADVRG", "CRR"),
  GASBE("GASBE", "GSB"),
  CMABI("CMABI", "CMA"),
  ADFBI("ADFBI", "MSC"),
  ORFBI("ORFBI", "ORF"),
  TAFBI("TAFBI", "TAF"),
  FREGN("FREGN", "FRG"),
  ADFRF("ADFRF", "ADF"),
  EDUCA("EDUCA","EDU"),
  REGT("REGT", "RGT"),
  APIBI("APIBI", "API"),
  IARCE("IARCE", "ICE"),
  RGFEE("RGFEE", "RGF");

  private final String value;
  private final String shortName;

  BuCodeEnum(String value, String shortName) {

    this.value = value;
    this.shortName = shortName;
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

  public static String getInvoiceTypeName(String value) {
    for (BuCodeEnum status : BuCodeEnum.values()) {
      if (status.getValue().equals(value)) {
        return status.name();
      }
    }
    return null;
  }

  public static String getInvoiceTypeNameByShortName(String shortName) {
    for (BuCodeEnum status : BuCodeEnum.values()) {
      if (status.getShortName().equals(shortName)) {
        return status.name();
      }
    }
    return null;
  }

  public String getValue() {
    return value;
  }
  public String getShortName(){ return shortName; }
}
