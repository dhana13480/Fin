package org.finra.rmcs.common.constants;

public enum ReceivableStatus {
  RECEIVED(11, true) {
    @Override
    public ReceivableStatus nextState() {
      return ReceivableStatus.READY_TO_BILL;
    }
  },
  INVALID(12, true) {
    @Override
    public ReceivableStatus nextState() {
      return ReceivableStatus.INVALID;
    }
  },
  SUSPEND(13, true) {
    @Override
    public ReceivableStatus nextState() {
      return ReceivableStatus.SUSPEND;
    }
  },
  READY_TO_BILL(14, false) {
    @Override
    public ReceivableStatus nextState() {
      return ReceivableStatus.INVOICED;
    }
  },
  INVOICED(15, false) {
    @Override
    public ReceivableStatus nextState() {
      return ReceivableStatus.SENT_TO_WD;
    }
  },
  SENT_TO_WD(16, false) {
    @Override
    public ReceivableStatus nextState() {
      return ReceivableStatus.SENT_TO_WD;
    }
  };

  private Integer id;
  private boolean errorStatus;

  ReceivableStatus(Integer id, boolean errorStatus) {
    this.id = id;
    this.errorStatus = errorStatus;
  }

  public static ReceivableStatus getEnumById(Integer id) {
    for (ReceivableStatus e : values()) {
      if (e.id != null && e.id.equals(id)) {
        return e;
      }
    }
    return null;
  }

  public static Integer getNextStatusId(Integer id) {
    ReceivableStatus status = getEnumById(id);
    if (status != null) {
      return status.nextState().id;
    }
    return id;
  }

  public Integer getId() {
    return id;
  }

  public static boolean isErrorStatus(Integer id) {
    ReceivableStatus status = getEnumById(id);
    if (status != null) {
      return status.errorStatus;
    }
    return false;
  }

  public abstract ReceivableStatus nextState();
}
