package org.finra.rmcs.common.constants;

import java.util.Arrays;
import java.util.List;

public enum RevenueStreamEnum {
  // Add more when needed
  APIBI("API"),
  ATRBI("ATR"),
  COBRA("POS"),
  ADVRG("CRR"),
  MQPBI("MQP"),
  IARCE("ICE"),
  IARCE_FORM("ICE"),
  CRDRG("CRG"),
  EMSBU("EMS"),
  CPACT("CPT"),
  CRDRN("CRN"),
  DROCL("DRO"),
  DRCAT("DRC"),
  ADFBI("MSC"),
  ADFRF("ADF"),
  CMABI("CMA"),
  EDUCA("EDU"),
  FREGN("FRG"),
  GASBE("GSB"),
  MREGN("MRG"),
  ORFBI("ORF"),
  TAFBI("TAF"),
  TRACE("TRC"),
  MTRCS("DRF"),
  RGFEE("RGF"),
  REGT("RGT");

  private final String shortCode;

  RevenueStreamEnum(String shortCode) {
    this.shortCode = shortCode;
  }

  public String getShortCode() {
    return this.shortCode;
  }

  public static RevenueStreamEnum getRevenueStreamEnum(String revenueStream) {
    List<RevenueStreamEnum> list =
        Arrays.stream(RevenueStreamEnum.values())
            .filter(f -> f.name().equals(revenueStream))
            .toList();
    return list.get(0);
  }
}
