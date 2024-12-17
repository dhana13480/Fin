package org.finra.rmcs.constants;

public enum StatusEnum {
  DATA_REFRESH_LOG_SUCCESS(6, true) {
    @Override
    public StatusEnum nextState() {
      return StatusEnum.DATA_REFRESH_LOG_SUCCESS;
    }
  },
  RUN_DATA_REFRESH_SUCCESS(7, true) {
    @Override
    public StatusEnum nextState() {
      return StatusEnum.RUN_DATA_REFRESH_SUCCESS;
    }
  },
  RUN_DATA_REFRESH_TIME_FOUR_THIRTY_EXCEED(8, true) {
    @Override
    public StatusEnum nextState() {
      return StatusEnum.RUN_DATA_REFRESH_TIME_FOUR_THIRTY_EXCEED;
    }
  },
  RUN_DATA_REFRESH_TIME_SEVEN_EXCEED(9, true) {
    @Override
    public StatusEnum nextState() {
      return StatusEnum.RUN_DATA_REFRESH_TIME_SEVEN_EXCEED;
    }
  };

  private Integer id;
  private boolean errorStatus;

  StatusEnum(Integer id, boolean errorStatus) {
    this.id = id;
    this.errorStatus = errorStatus;
  }

  public Integer getId() {
    return id;
  }

  public static StatusEnum getEnumById(Integer id) {
    for (StatusEnum e : values()) {
      if ((null != id) && (null != e.id) && (e.id.equals(id))) {
        return e;
      }
    }

    return null;
  }

  public static Integer getNextStatusId(Integer id) {
    StatusEnum status = getEnumById(id);
    if (status != null) {
      return status.nextState().id;
    }
    return id;
  }

  public static boolean isErrorStatus(Integer id) {
    StatusEnum status = getEnumById(id);
    if (status != null) {
      return status.errorStatus;
    }
    return false;
  }

  public abstract StatusEnum nextState();
}
