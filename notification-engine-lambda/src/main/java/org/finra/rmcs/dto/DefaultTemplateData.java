package org.finra.rmcs.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class DefaultTemplateData {

  @JsonProperty(value = "name")
  String name;
  @JsonProperty(value = "title")
  String title;
  @JsonProperty(value = "body")
  String body;
  @JsonProperty(value = "actionLink")
  String actionLink;
  @JsonProperty(value = "supportName")
  String supportName;
  @JsonProperty(value = "supportContact")
  String supportContact;
  @JsonProperty(value = "supportDayStart")
  String supportDayStart;
  @JsonProperty(value = "supportDayEnd")
  String supportDayEnd;
  @JsonProperty(value = "supportTimeStart")
  String supportTimeStart;
  @JsonProperty(value = "supportTimeEnd")
  String supportTimeEnd;
  @JsonProperty(value = "u4RequestGroup")
  String u4RequestGroup;
  @JsonProperty(value = "completionDate")
  String completionDate;

  @JsonProperty(value = "feedback")
  String feedback;

}
