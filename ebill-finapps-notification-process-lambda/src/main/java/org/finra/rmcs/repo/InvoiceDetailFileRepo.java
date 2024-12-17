package org.finra.rmcs.repo;

import org.finra.rmcs.entity.InvoiceDetailFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InvoiceDetailFileRepo extends JpaRepository<InvoiceDetailFileEntity, UUID> {

    InvoiceDetailFileEntity findOneByInvoiceNumberAndFileTypeAndSubPartition(String partitionValue, String businessObjectFormatFileType, String s);
}
