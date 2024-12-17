package org.finra.rmcs.constants;

public enum AlertStatusEnum {

    BALANCEABOVE(2,true){
        @Override
        public AlertStatusEnum nextState() {
            return AlertStatusEnum.BALANCEABOVE;
        }

    },
    BALANCEBELOW(1,true){
        @Override
        public AlertStatusEnum nextState() {
            return AlertStatusEnum.BALANCEBELOW;
        }

    },
    DEBIT(3,true){
        @Override
        public AlertStatusEnum nextState() {
            return AlertStatusEnum.DEBIT;
        }

    },
    INVOICE_PAST_DUE(4,true){
        @Override
        public AlertStatusEnum nextState() {
            return AlertStatusEnum.INVOICE_PAST_DUE;
        }

    },
    NEW_INVOICE_AVAILABLE(5,true){
        @Override
        public AlertStatusEnum nextState() {
            return AlertStatusEnum.NEW_INVOICE_AVAILABLE;
        }

    },
    DATA_REFRESH_LOG_SUCCESS(6,true){
        @Override
        public AlertStatusEnum nextState() {
            return AlertStatusEnum.DATA_REFRESH_LOG_SUCCESS;
        }

    },
    RUN_DATA_REFRESH_SUCCESS(7,true){
        @Override
        public AlertStatusEnum nextState() {
            return AlertStatusEnum.RUN_DATA_REFRESH_SUCCESS;
        }
    },
    RUN_DATA_REFRESH_TIME_FOUR_THIRTY_EXCEED(8,true){
        @Override
        public AlertStatusEnum nextState() {return AlertStatusEnum.RUN_DATA_REFRESH_TIME_FOUR_THIRTY_EXCEED;}
    },
    RUN_DATA_REFRESH_TIME_SEVEN_EXCEED(9,true){
        @Override
        public AlertStatusEnum nextState() {return AlertStatusEnum.RUN_DATA_REFRESH_TIME_SEVEN_EXCEED;}
    };

    private Integer id;
    private boolean errorStatus;
    AlertStatusEnum(Integer id, boolean errorStatus) {
        this.id = id;
        this.errorStatus = errorStatus;
    }

    public Integer getId() {
        return id;
    }

    public static AlertStatusEnum getEnumById(Integer id) {
        for (AlertStatusEnum e: values()) {
            if ((null != id) && (null != e.id) && (e.id.equals(id))) {
                return e;
            }
        }

        return null;
    }

    public static Integer getNextStatusId(Integer id) {
        AlertStatusEnum status = getEnumById(id);
        if (status != null) {
            return status.nextState().id;
        }
        return id;
    }

    public static boolean isErrorStatus(Integer id) {
        AlertStatusEnum status = getEnumById(id);
        if (status != null) {
            return status.errorStatus;
        }
        return false;
    }

    public abstract AlertStatusEnum nextState();

}
