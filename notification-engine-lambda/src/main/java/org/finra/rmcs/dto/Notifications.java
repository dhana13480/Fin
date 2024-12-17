package org.finra.rmcs.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Data
public class Notifications {

  @JsonProperty(value = "sourceApplication")
  private String sourceApplication;

  @JsonProperty(value = "activeDate")
  private String activeDate;

  @JsonProperty(value = "expirationDate")
  private String expirationDate;

  @JsonProperty(value = "type")
  private String type;

  @JsonProperty(value = "actionable")
  private boolean actionable;


  @JsonProperty(value = "urgencyIndicator")
  private boolean urgencyIndicator;

  @JsonProperty(value = "publishedStatus")
  private String publishedStatus;

  @JsonProperty(value = "applicationDelivery")
  private ApplicationDelivery applicationDelivery;

  @JsonProperty(value = "record")
  private boolean record;

  @JsonProperty(value = "templateName")
  private String templateName;

  @JsonProperty(value = "templateVersion")
  private Integer templateVersion;

  @JsonProperty(value = "templateReplacementData")
  private TemplateReplacementData templateReplacementData;

  @JsonProperty(value = "deliverEmail")
  private boolean deliverEmail;

  @JsonProperty(value = "subscriptionGroupName")
  private String subscriptionGroupName;

  @JsonProperty(value = "subscriptionTypeName")
  private String subscriptionTypeName;

  @JsonProperty(value = "emailSecondaryRecipient")
  private EmailSecondaryRecipient emailSecondaryRecipient;

}
