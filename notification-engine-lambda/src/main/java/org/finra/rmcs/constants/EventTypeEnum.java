package org.finra.rmcs.constants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum EventTypeEnum {

    AFFILIATED_FIRM_TRANSFER_SUBMITTED("AFFILIATED_FIRM_TRANSFER_SUBMITTED"),

    AFFILIATED_FIRM_TRANSFER_PROCESSED("AFFILIATED_FIRM_TRANSFER_PROCESSED"),

    AFFILIATED_FIRM_TRANSFER_FAILED("AFFILIATED_FIRM_TRANSFER_FAILED"),

    AFFILIATED_FIRM_TRANSFER_PROCESSING_ERROR("AFFILIATED_FIRM_TRANSFER_PROCESSING_ERROR"),
    AFFILIATED_FIRM_TRANSFER_QUEUED("AFFILIATED_FIRM_TRANSFER_QUEUED"),
    CRDRG_BALANCEBELOW("CRDRG_BALANCEBELOW"),
    CRDRG_BALANCEABOVE("CRDRG_BALANCEABOVE"),
    CRDRG_DEBIT("CRDRG_DEBIT"),
    AUTOPAY_SETUP("AUTOPAY_SETUP"),
    AUTOPAY_DELETE_SYSTEM_INITIATE("AUTOPAY_DELETE_SYSTEM_INITIATE"),
    AUTOPAY_DELETE_PAYMENT_FAILED("AUTOPAY_DELETE_PAYMENT_FAILED"),
    AUTOPAY_DELETE_USER_INITIATE("AUTOPAY_DELETE_USER_INITIATE"),
    AUTOPAY_PAYMENT_FAILED("AUTOPAY_PAYMENT_FAILED"),
    AUTOPAY_PAYMENT_SUCCESS("AUTOPAY_PAYMENT_SUCCESS"),
    REFUND_SUBMITTED("REFUND_SUBMITTED"),
    NEW_INVOICE_AVAILABLE("NEW_INVOICE_AVAILABLE"),
    INVOICE_AUTOPAY_DELETE_SYSTEM_INITIATE("INVOICE_AUTOPAY_DELETE_SYSTEM_INITIATE"),
    INVOICE_AUTOPAY_DELETE_PAYMENT_FAILED("INVOICE_AUTOPAY_DELETE_PAYMENT_FAILED"),
    INVOICE_AUTOPAY_SETUP("INVOICE_AUTOPAY_SETUP"),
    INVOICE_AUTOPAY_DELETE_USER_INITIATE("INVOICE_AUTOPAY_DELETE_USER_INITIATE"),
    INVOICE_PAST_DUE("INVOICE_PAST_DUE"),
    ENABLER_RUN_DATA_REFRESH_SUCCESS("ENABLER_RUN_DATA_REFRESH_SUCCESS"),
    ENABLER_RUN_DATA_REFRESH_TIME_FOUR_THIRTY_EXCEED("ENABLER_RUN_DATA_REFRESH_TIME_FOUR_THIRTY_EXCEED"),
    ENABLER_RUN_DATA_REFRESH_TIME_SEVEN_EXCEED("ENABLER_RUN_DATA_REFRESH_TIME_SEVEN_EXCEED"),
    ENABLER_DATA_REFRESH_LOG_SUCCESS("ENABLER_DATA_REFRESH_LOG_SUCCESS"),
    REALLOCATION_INVOICE_SUBMITTED("REALLOCATION_INVOICE_SUBMITTED"),
    REALLOCATION_INVOICE_PROCESSED("REALLOCATION_INVOICE_PROCESSED"),
    REALLOCATION_INVOICE_FAILED("REALLOCATION_INVOICE_FAILED"),
    REALLOCATION_INVOICE_QUEUED("REALLOCATION_INVOICE_QUEUED");


    private final String value;

    EventTypeEnum(String value) {
        this.value = value;
    }

    public static String findbyPaymentTypeAndEventName(String module,String status ) {
        log.info("status {} and module{} ", status , module);
        for (EventTypeEnum eventTypeEnum : values()) {
            if (eventTypeEnum.name().equalsIgnoreCase(module + "_" + status)) {
                log.info("eventTypeEnum {} ", eventTypeEnum.name());
                return eventTypeEnum.name();
            }
        }
        return null;

    }

    public String getValue() {
        return value;
    }
}


