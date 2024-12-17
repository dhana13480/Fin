package org.finra.rmcs.model;

import java.util.Map;
import lombok.Data;
import org.finra.herd.sdk.model.BusinessObjectDataKey;

@Data
public class BusinessObjectDataStatusChangeEvent {

  private BusinessObjectDataKey businessObjectDataKey;
  private String eventDate;
  private String newBusinessObjectDataStatus;
  private String oldBusinessObjectDataStatus;
  private Map<String, String> attributes;
}
