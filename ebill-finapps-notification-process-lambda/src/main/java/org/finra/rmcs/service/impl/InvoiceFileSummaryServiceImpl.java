package org.finra.rmcs.service.impl;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finra.rmcs.dto.BusinessObjectData;
import org.finra.rmcs.dto.DmNotification;
import org.finra.rmcs.entity.InvoiceDetailFileEntity;
import org.finra.rmcs.entity.InvoiceSummaryFileEntity;
import org.finra.rmcs.repo.InvoiceDetailFileRepo;
import org.finra.rmcs.repo.InvoiceSummaryFileRepo;
import org.finra.rmcs.service.DmFileService;
import org.finra.rmcs.service.InvoiceFileSummaryService;
import org.finra.rmcs.utils.Util;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class InvoiceFileSummaryServiceImpl implements InvoiceFileSummaryService {
  private InvoiceDetailFileRepo invoiceDetailFileRepo;
  private InvoiceSummaryFileRepo invoiceSummaryFileRepo;

  private DmFileService dmFileService;
  public void storeDmNotificationIntoInvoiceFileSummary(String correlationId, DmNotification notificationMessage){
    log.info("start storeDmNotificationIntoInvoiceFileSummary with correlationId:{}", correlationId);
    try{
      if(Util.isPdfFileTypeNotification(notificationMessage)){
        BusinessObjectData dmFileInformation = dmFileService.getDmFileInformation(correlationId, notificationMessage);
        //fetch existing record
        InvoiceSummaryFileEntity invoiceSummaryFileEntity = invoiceSummaryFileRepo.findOneByInvoiceNumber(notificationMessage.getBusinessObjectDataKey().getPartitionValue());
        //save all pdf file information in invoice summary file entity
        invoiceSummaryFileEntity = Util.convertDmNotificationMessageToInvoiceSummaryFileEntity(invoiceSummaryFileEntity, notificationMessage, dmFileInformation);
        invoiceSummaryFileRepo.save(invoiceSummaryFileEntity);
      }else if(Util.isValidGZorZipFileTypeNotification(notificationMessage)){
        BusinessObjectData dmFileInformation = dmFileService.getDmFileInformation(correlationId, notificationMessage);
        //fetch existing record
        String subPartitionValue = Util.getSubPartitionValue(notificationMessage);
        InvoiceDetailFileEntity invoiceDetailFileEntity = invoiceDetailFileRepo.findOneByInvoiceNumberAndFileTypeAndSubPartition(notificationMessage.getBusinessObjectDataKey().getPartitionValue(), notificationMessage.getBusinessObjectDataKey().getBusinessObjectFormatFileType(), subPartitionValue);

        //save all gz and zip file information in invoice detail file entity
       invoiceDetailFileEntity = Util.convertDmNotificationMessageToInvoiceDetailsFileEntity(invoiceDetailFileEntity, notificationMessage, dmFileInformation);
       invoiceDetailFileRepo.save(invoiceDetailFileEntity);
      }
    }catch (Exception ex){
      log.error("Exception during persisting the file data, exception:{}", ex);
      throw new RuntimeException(ex);
    }

    log.info("completed storeDmNotificationIntoInvoiceFileSummary with correlationId:{}", correlationId);
  }
}

