package org.finra.rmcs.constants;

import java.util.Arrays;
import java.util.List;

public class ReceivableAuditConstants {
    public static final String RMCS_IN_BOUND = "RMCS Inbound";
    public static final String RMCS_PROCESS = "RMCS Process";
    public static final String RMCS_OUT_BOUND = "RMCS OutBound";
    public static final String RMCS_FIELD_REPORT_CHECK_AMOUNT = "Amount";
    public static final String RMCS_FIELD_REPORT_CHECK_COUNT = "Count";
    public static final String RMCS_IN_BOUND_REJECTION_DETAILS = "Rejection Details";
    public static final String RMCS_PROCESS_FAILURE_DETAILS = "Failure Details";
    public static final String ICM_FILE_NAME = "ICM_RMCS";

    public static final String ICM_REPORT_SUBJECT = "ICM Report";
    public static final String EMAIL_BODY_DATA_TXT =
        "Please review attached ICM Report for {date}. Please contact {contactAddress} if you have any questions regarding the report.";
    public static final String EMAIL_BODY_NO_DATA_TXT =
        "There is no data available for {date}. Please contact {contactAddress} if you have any questions regarding the email.";

    public static final String[] ICM_INBOUND_HEADERS = {
        "Date", "Source", "Destination", "RevenueStream", "TransactionType", "Check", "SourceData", "DestinationData", "Rejected", "Difference", "Result"
    };

    public static final String[] ICM_PROCESSING_HEADERS = {
        "Date", "Source", "Destination", "RevenueStream", "TransactionType", "Check", "SourceData", "DestinationData", "Data not picked due to business reasons", "Difference", "Result"
    };

    public static final String[] ICM_OUT_BOUND_HEADERS = {
        "Date", "Source", "Destination", "RevenueStream", "TransactionType", "Check", "SourceData", "Destination Data", "Difference", "Result"
    };
    public static final String[] ICM_IN_PROCESS_SUB_HEADERS = {
        "ID", "Revenue Stream", "Rejection Reason", "Receivable Amount"
    };

    public static final String[] ICM_PROCESS_SUB_HEADERS = {
        "ID", "Revenue Stream", "Failure Reason", "Receivable Amount"
    };

    public static final String[] ICM_INBOUND_DATA = {"LOB", "RMCS", "Receivables"};

    public static final String[] ICM_IN_PROCESS_DATA = {"RMCS", "RMCS", "Receivable-To-Invoice"};

    public static final String[] ICM_RECEIVABLE_OUTBOUND_DATA = {"RMCS", "WORKDAY", "Invoice"};
    public static final String[] ICM_PAYMENT_OUTBOUND_DATA = {"RMCS", "WORKDAY", "Payment"};

    //EMSBU is not required to send to WD and IARCE_FORM will be combined as IARCE
    public static final List<String> IGNORE_REVENUE_STREAM_LIST = Arrays.asList("IARCE_FORM", "EMSBU");

    public static final String SUCCESS = "Success";
    public static final String FAILURE = "Failure";

    public static final String ZERO = "0";
    public static final String ONE = "1";
    public static final String HYPHEN = "-";
    public static final String ICM_AFT_SOURCE = "EBILL";
    public static final String ICM_AFT_DESTINATION = "WORKDAY";
    public static final String ICM_AFT_TRANSACTION_TYPE = "EBRTO";
    public static final String ICM_AFT_CHECK = "Amount";
    public static final String ICM_AFT_COUNT = "Count";
}
