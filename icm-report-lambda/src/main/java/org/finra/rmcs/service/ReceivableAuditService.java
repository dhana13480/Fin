package org.finra.rmcs.service;

import java.time.ZonedDateTime;
import java.util.List;
import org.finra.rmcs.dto.ReceivableAudit;

public interface ReceivableAuditService {

    List<ReceivableAudit> findByAuditEntryCreatedDate(ZonedDateTime entryDate);

}
