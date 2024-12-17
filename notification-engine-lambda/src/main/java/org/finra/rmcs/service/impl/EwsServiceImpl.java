package org.finra.rmcs.service.impl;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.finra.rmcs.dto.EwsAccountInformationResponse;
import org.finra.rmcs.service.EwsSerrvice;
import org.finra.rmcs.utils.Util;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.Objects;

@Service
@Slf4j
public class EwsServiceImpl implements EwsSerrvice {
  private Util utils;

  public EwsServiceImpl(@NonNull Util utils) {
    Objects.requireNonNull(utils);

    this.utils = utils;
  }

  @Override
  public ResponseEntity<EwsAccountInformationResponse> getAccountInformation(String ewsUser) {

    try {
      if (StringUtils.isBlank(ewsUser)) {
        throw new IllegalArgumentException("EwsUser");
      }

      EwsAccountInformationResponse ewsAccountInformationResponse =
          utils.getEwsAccountInformation(ewsUser);

      return new ResponseEntity<>(ewsAccountInformationResponse, HttpStatus.OK);
    } catch (Exception exception) {
      String message =
          String.format(
              "getAccountInformation failed. ewsUser[%s] exception[%s]", ewsUser, exception);
      log.error(message);

      return new ResponseEntity<>(new EwsAccountInformationResponse(), getStatusCode(exception));
    }
  }

  private HttpStatus getStatusCode(Exception exception) {

    if (exception instanceof HttpStatusCodeException) {
        return HttpStatus.resolve(((HttpStatusCodeException) exception).getStatusCode().value());
    }
    else if (exception instanceof IllegalArgumentException) {
      return HttpStatus.BAD_REQUEST;
    }
    else {
      return HttpStatus.INTERNAL_SERVER_ERROR;
    }
  }
}
