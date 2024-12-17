package org.finra.rmcs.repo;

import org.finra.rmcs.entity.InvoiceSummaryFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InvoiceSummaryFileRepo extends JpaRepository<InvoiceSummaryFileEntity, UUID> {

    InvoiceSummaryFileEntity findOneByInvoiceNumber(String invoiceNumber);
}
