package org.finra.rmcs.constants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum AlertEventStatusEnum {
    NEW(1),
    PROCESSED(2),
    ERROR(3);
    private final Integer value;
    AlertEventStatusEnum(Integer value) {
        this.value = value;
    }
    public Integer getValue() {
        return value;
    }
}
