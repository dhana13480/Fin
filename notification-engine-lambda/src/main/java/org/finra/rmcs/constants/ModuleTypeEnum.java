package org.finra.rmcs.constants;

public enum ModuleTypeEnum {
    NONE(0, false) {
        @Override
        public ModuleTypeEnum nextState() {
            return ModuleTypeEnum.NONE;
        }
    },

    // Please consider using
    // https://bitbucket.finra.org/projects/RMCS/repos/rmcs-lambda-function/browse/ebill-batch-process-lambda/src/main/java/org/finra/rmcs/domain/PaymentType.java
    // The class is fully populated and the class name is derived from databse table.
    AFFILIATED_FIRM_TRANSFER(5, true) {
        @Override
        public ModuleTypeEnum nextState() {
            return ModuleTypeEnum.AFFILIATED_FIRM_TRANSFER;
        }
    },

    AUTOPAY(10, true) {
        @Override
        public ModuleTypeEnum nextState() {
            return ModuleTypeEnum.AUTOPAY;
        }
    },
    INVOICE_AUTOPAY(11, true) {
        @Override
        public ModuleTypeEnum nextState() {
            return ModuleTypeEnum.INVOICE_AUTOPAY;
        }
    },
    REALLOCATION_INVOICE(3, true) {
        @Override
        public ModuleTypeEnum nextState() {return ModuleTypeEnum.REALLOCATION_INVOICE;
        }
    };

    private Integer id;
    private boolean errorType;

    ModuleTypeEnum(Integer id, boolean errorType) {
        this.id = id;
        this.errorType = errorType;
    }

    public Integer getId() {
        return id;
    }

    public static ModuleTypeEnum getEnumById(Integer id) {
        for (ModuleTypeEnum e: values()) {
            if ((null != id) && (null != e.id) && (e.id.equals(id))) {
              return e;
            }
        }

        return ModuleTypeEnum.NONE;
    }

    public static Integer getNextTypeId(Integer id) {
        ModuleTypeEnum status = getEnumById(id);
        if (status != null) {
            return status.nextState().id;
        }
        return id;
    }

    public static boolean isErrorType(Integer id) {
        ModuleTypeEnum status = getEnumById(id);
        if (status != null) {
            return status.errorType;
        }
        return false;
    }

    public abstract ModuleTypeEnum nextState();
}
