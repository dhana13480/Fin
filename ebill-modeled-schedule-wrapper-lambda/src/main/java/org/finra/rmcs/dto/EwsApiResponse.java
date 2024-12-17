package org.finra.rmcs.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties
public class EwsApiResponse {
  private String accountId;

  private String orgId;

  private String orgClass;

  private PersonalInfo personalInfo;

  private String authnType;

  private String portalType;

  private boolean isDeleted;

  private DateFormat createdDate;

  private String createdBy;

  private DateFormat lastUpdatedDate;

  private String lastUpdatedBy;
}
