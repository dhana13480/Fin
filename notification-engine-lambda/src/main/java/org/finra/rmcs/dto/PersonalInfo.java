package org.finra.rmcs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalInfo {
  private String firstName;

  private String middleName;

  private String lastName;

  private String suffix;

  private String email;

  private String phone;

}
