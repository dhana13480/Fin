package org.finra.rmcs.constants;

public enum WorkdayStatusEnum {
  INITIATED("INITIATED"),
  RECEIVED("RECEIVED");

  private String status;
  WorkdayStatusEnum(String status) {
    this.status = status;
  }

  public String getStatus(){
    return this.status;
  }
}
