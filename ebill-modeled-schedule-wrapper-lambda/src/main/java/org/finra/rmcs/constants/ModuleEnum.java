package org.finra.rmcs.constants;

public enum ModuleEnum {

    CRDRG(1, true) {
        @Override
        public ModuleEnum nextState() {
            return ModuleEnum.CRDRG;
        }
    },
    ADFBI(2, true) {
        @Override
        public ModuleEnum nextState() {
            return ModuleEnum.ADFBI;
        }
    },
    ADFRF(3, true) {
        @Override
        public ModuleEnum nextState() {
            return ModuleEnum.ADFRF;
        }
    },
    ADVRG(4, true) {
        @Override
        public ModuleEnum nextState() {
            return ModuleEnum.ADVRG;
        }
    },
    APIBI(5, true) {
        @Override
        public ModuleEnum nextState() {
            return ModuleEnum.APIBI;
        }
    },
    CMABI(6, true) {
        @Override
        public ModuleEnum nextState() {
            return ModuleEnum.CMABI;
        }
    },
    EDUCA(7, true) {
        @Override
        public ModuleEnum nextState() {
            return ModuleEnum.EDUCA;
        }
    },
    CRDRN(8, true) {
        @Override
        public ModuleEnum nextState() {
            return ModuleEnum.CRDRN;
        }
    },FREGN(9, true) {
        @Override
        public ModuleEnum nextState() {
            return ModuleEnum.FREGN;
        }
    },
    GASBE(10, true) {
        @Override
        public ModuleEnum nextState() {
            return ModuleEnum.GASBE;
        }
    },
    IARCE(11, true) {
        @Override
        public ModuleEnum nextState() {
            return ModuleEnum.IARCE;
        }
    },MREGN(12, true) {
        @Override
        public ModuleEnum nextState() {
            return ModuleEnum.MREGN;
        }
    },
    MTRCS(13, true) {
        @Override
        public ModuleEnum nextState() {
            return ModuleEnum.MTRCS;
        }
    },
    ORFBI(14, true) {
        @Override
        public ModuleEnum nextState() {
            return ModuleEnum.ORFBI;
        }
    },
    TAFBI(15, true) {
        @Override
        public ModuleEnum nextState() {
            return ModuleEnum.TAFBI;
        }
    },
    TRACE(16, true) {
        @Override
        public ModuleEnum nextState() {
            return ModuleEnum.TRACE;
        }
    },
    ENABLER(17, true) {
        @Override
        public ModuleEnum nextState() {
            return ModuleEnum.ENABLER;
        }
    };

    private Integer id;
    private boolean errorType;

    ModuleEnum(Integer id, boolean errorType) {
        this.id = id;
        this.errorType = errorType;
    }

    public static ModuleEnum getEnumById(Integer id) {
        for (ModuleEnum e : values()) {
            if ((null != id) && (null != e.id) && (e.id.equals(id))) {
                return e;
            }
        }

        return null;
    }

    public static Integer getNextTypeId(Integer id) {
        ModuleEnum status = getEnumById(id);
        if (status != null) {
            return status.nextState().id;
        }
        return id;
    }

    public static boolean isErrorType(Integer id) {
        ModuleEnum status = getEnumById(id);
        if (status != null) {
            return status.errorType;
        }
        return false;
    }

    public Integer getId() {
        return id;
    }

    public abstract ModuleEnum nextState();

}