package org.finra.rmcs.constants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum PaymentStatusEnum {

    None("0"),
    Authorized("1"),
    Processed("2"),
    AuthorizationFailed("3"),
    OnHold("4"),
    Expired("5"),
    ProcessingError("6"),
    Submitted("7"),
    FAILED("8"),
    Pending("9"),
    RETURN("10"),
    PAID("11"),
    READY("12");
 public final String value;

    private static final Map<String, PaymentStatusEnum> paymentStatus = new HashMap<>();
     static{
         for (PaymentStatusEnum p : values()){
             paymentStatus.put(p.value, p);
         }
     }

     public static PaymentStatusEnum valueOfPaymentStatus(String value){
         return paymentStatus.get(value);

     }


    PaymentStatusEnum(String value) {
        this.value = value;
    }

    public static String findByStringValue(String type) {
        return Arrays.stream(PaymentStatusEnum.values()).filter(it -> it.name().contains(type))
                .findFirst()
                .map(PaymentStatusEnum::getValue)
                .orElse(null);
    }
    public static String getPaymentStatusName(String value){
        for (PaymentStatusEnum status: PaymentStatusEnum.values()){
            if (status.getValue().equals(value)){
                return status.name();
            }
        }
        return null;
    }
    public String getValue() {
        return value;
    }


}
