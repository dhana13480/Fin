package org.finra.rmcs.service;

import java.sql.Timestamp;
import java.util.Arrays;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.constants.FileTypeEnum;
import org.finra.rmcs.dto.BusinessObjectData;
import org.finra.rmcs.dto.BusinessObjectDataKey;
import org.finra.rmcs.dto.DmNotification;
import org.finra.rmcs.dto.StorageFile;
import org.finra.rmcs.dto.StorageUnit;
import org.finra.rmcs.entity.InvoiceDetailFileEntity;
import org.finra.rmcs.entity.InvoiceSummaryFileEntity;
import org.finra.rmcs.repo.InvoiceDetailFileRepo;
import org.finra.rmcs.repo.InvoiceSummaryFileRepo;
import org.finra.rmcs.service.impl.DmFileServiceImpl;
import org.finra.rmcs.service.impl.InvoiceFileSummaryServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
public class InvoiceFileSummaryServiceTest {

  @InjectMocks
  InvoiceFileSummaryServiceImpl invoiceFileSummaryService;
  @Mock
  private InvoiceDetailFileRepo invoiceDetailFileRepo;
  @Mock
  private InvoiceSummaryFileRepo invoiceSummaryFileRepo;

  @Mock
  private DmFileServiceImpl dmFileService;

  DmNotification pdfNotificationMessage;
  DmNotification gzNotificationMessage;

  BusinessObjectData dmBusinessObjectDataResponse;
  @BeforeEach
  public void setup(){
    pdfNotificationMessage = DmNotification.builder().eventDate(new Timestamp(System.currentTimeMillis()))
        .businessObjectDataKey(BusinessObjectDataKey.builder().businessObjectFormatFileType(
            FileTypeEnum.PDF.name()).businessObjectDataVersion(0).build()).newBusinessObjectDataStatus(Constants.VALID_BUSINESS_OBJECT_STATUS).build();

    gzNotificationMessage = DmNotification.builder().eventDate(new Timestamp(System.currentTimeMillis()))
        .businessObjectDataKey(BusinessObjectDataKey.builder().businessObjectFormatFileType(
            FileTypeEnum.GZ.name()).businessObjectDataVersion(0).build()).newBusinessObjectDataStatus(Constants.VALID_BUSINESS_OBJECT_STATUS).build();
    dmBusinessObjectDataResponse = BusinessObjectData.builder()
        .id(123).businessObjectDefinitionName("RGFEE").businessObjectFormatUsage("PRC").businessObjectFormatFileType("PDF")
        .storageUnits(Arrays.asList(StorageUnit.builder().storageFiles(Arrays.asList(
            StorageFile.builder().filePath(
                    "finapps-int/erp/prc/gz/rgfee/schm-v0/data-v0/invoice-id=RGF17803548/trade-date=2023-12-14/RGFEE-2023-12-14-RGF17803548.GZ")
                .build())).build())).build();
  }

  @Test
  public void storeDmNotificationIntoInvoiceFileSummaryTest(){
    Mockito.when(invoiceSummaryFileRepo.save(Mockito.any())).thenReturn(new InvoiceSummaryFileEntity());
    Mockito.when(dmFileService.getDmFileInformation(Mockito.any(), Mockito.any())).thenReturn(dmBusinessObjectDataResponse);
    invoiceFileSummaryService.storeDmNotificationIntoInvoiceFileSummary("12441", pdfNotificationMessage);
    Mockito.verify(invoiceSummaryFileRepo).save(Mockito.any());
  }

  @Test
  public void storeDmNotificationIntoInvoiceDetailFileTest(){
    Mockito.when(invoiceDetailFileRepo.save(Mockito.any())).thenReturn(new InvoiceDetailFileEntity());
    Mockito.when(dmFileService.getDmFileInformation(Mockito.any(), Mockito.any())).thenReturn(dmBusinessObjectDataResponse);
    invoiceFileSummaryService.storeDmNotificationIntoInvoiceFileSummary("12441", gzNotificationMessage);
    Mockito.verify(invoiceDetailFileRepo).save(Mockito.any());
  }

  @Test
  public void storeDmNotificationIntoInvoiceFileSummaryExceptionTest(){
    Mockito.when(invoiceSummaryFileRepo.save(Mockito.any())).thenThrow(new RuntimeException());
    Assertions.assertThrows(RuntimeException.class, () -> {
      invoiceFileSummaryService.storeDmNotificationIntoInvoiceFileSummary("12345", pdfNotificationMessage);
    });
  }




}
