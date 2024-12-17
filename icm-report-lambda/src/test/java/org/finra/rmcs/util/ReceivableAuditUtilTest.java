//package org.finra.rmcs.util;
//
//import com.google.gson.Gson;
//import java.time.LocalDateTime;
//import java.util.List;
//import org.finra.rmcs.dto.ReceivableAudit;
//import org.finra.rmcs.dto.ReceivableReport;
//import org.junit.Assert;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.mockito.InjectMocks;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//public class ReceivableAuditUtilTest {
//
//    @InjectMocks
//    ReceivableAuditUtil receivableAuditUtil;
//
//    private static ReceivableAudit receivableAudit12;
//    private static ReceivableAudit receivableAudit14;
//    private static ReceivableAudit receivableAudit15;
//    private static ReceivableAudit receivableAudit16;
//    @BeforeClass
//    public static void init() throws Exception {
//        Gson gson = new Gson();
//        receivableAudit12 = gson.fromJson(TestUtil.getResourceContent("ReceivableStatus12.json"),ReceivableAudit.class);
//        receivableAudit14 = gson.fromJson(TestUtil.getResourceContent("ReceivableStatus14.json"),ReceivableAudit.class);
//        receivableAudit15 = gson.fromJson(TestUtil.getResourceContent("ReceivableStatus15.json"),ReceivableAudit.class);
//        receivableAudit16 = gson.fromJson(TestUtil.getResourceContent("ReceivableStatus16.json"),ReceivableAudit.class);
//    }
//
//    @Test
//    public void getRMCSInboundListTest() {
//    ReceivableAudit receivableAudit =
//        ReceivableAudit.builder()
//            .invoiceId(receivableAudit14.getInvoiceId())
//            .id(receivableAudit14.getId())
//            .status(receivableAudit14.getStatus())
//            .amount(receivableAudit14.getAmount())
//            .revenueStream(receivableAudit14.getRevenueStream())
//            .statusReason(receivableAudit14.getStatusReason())
//            .auditEntryCreatedDate(receivableAudit14.getAuditEntryCreatedDate().plusHours(1))
//            .build();
//        List<ReceivableReport> report = receivableAuditUtil.getRMCSInboundList(List.of(receivableAudit12,receivableAudit14,receivableAudit12,receivableAudit14,receivableAudit));
//        Assert.assertEquals(2, report.size());
//    }
//
//    @Test
//    public void getRMCSInboundSubListTest() {
//        List<ReceivableAudit> report = receivableAuditUtil.getRMCSInboundSubList(List.of(receivableAudit12,receivableAudit14,receivableAudit12,receivableAudit14));
//        Assert.assertEquals(1, report.size());
//    }
//
//    @Test
//    public void getRMCSProcessListTest() {
//        ReceivableAudit receivableAudit =
//            ReceivableAudit.builder()
//                .invoiceId(receivableAudit15.getInvoiceId())
//                .id(receivableAudit15.getId())
//                .status(receivableAudit15.getStatus())
//                .amount(receivableAudit15.getAmount())
//                .revenueStream(receivableAudit15.getRevenueStream())
//                .statusReason(receivableAudit15.getStatusReason())
//                .auditEntryCreatedDate(receivableAudit15.getAuditEntryCreatedDate().plusHours(1))
//                .build();
//        List<ReceivableReport> report = receivableAuditUtil.getRMCSProcessList(List.of(receivableAudit14,receivableAudit15,receivableAudit14,receivableAudit15,receivableAudit));
//        Assert.assertEquals(2, report.size());
//    }
//
//    @Test
//    public void getRMCSProcessSubListTest() {
//        List<ReceivableAudit> report = receivableAuditUtil.getRMCSProcessSubList(List.of(receivableAudit14,receivableAudit15));
//        Assert.assertEquals(1, report.size());
//    }
//
//    @Test
//    public void getRMCSOutboundListTest() {
//        ReceivableAudit receivableAudit =
//            ReceivableAudit.builder()
//                .invoiceId(receivableAudit16.getInvoiceId())
//                .id(receivableAudit16.getId())
//                .status(receivableAudit16.getStatus())
//                .amount(receivableAudit16.getAmount())
//                .revenueStream(receivableAudit16.getRevenueStream())
//                .statusReason(receivableAudit16.getStatusReason())
//                .auditEntryCreatedDate(receivableAudit16.getAuditEntryCreatedDate().plusHours(1))
//                .build();
//        List<ReceivableReport> report = receivableAuditUtil.getRMCSOutboundList(List.of(receivableAudit15,receivableAudit16,receivableAudit15,receivableAudit16,receivableAudit));
//        Assert.assertEquals(2, report.size());
//    }
//}
