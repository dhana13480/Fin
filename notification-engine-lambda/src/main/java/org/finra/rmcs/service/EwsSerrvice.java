package org.finra.rmcs.service;

import org.finra.rmcs.dto.EwsAccountInformationResponse;
import org.springframework.http.ResponseEntity;

public interface EwsSerrvice {
  ResponseEntity<EwsAccountInformationResponse> getAccountInformation(String ewsUser);
}
