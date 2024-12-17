package org.finra.rmcs.service;

import static org.mockito.Mockito.when;

import org.finra.rmcs.dto.EwsAccountInformationResponse;
import org.finra.rmcs.service.impl.EwsServiceImpl;
import org.finra.rmcs.utils.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.client.HttpClientErrorException;

@SpringJUnitConfig
public class EwsServiceImplTest {

  @InjectMocks
  private EwsServiceImpl ewsService;

  @Mock
  private Util utils;

  @BeforeEach
  public void before() {
    utils = Mockito.mock(Util.class);

    ewsService = new EwsServiceImpl(utils);
  }

  @Test
  public void getAccountInformationReturnsHttpStatusOk() {
    // Arrange
    String ewsUser = "ewsUser";
    HttpStatus httpStatus = HttpStatus.OK;
    EwsAccountInformationResponse ewsAccountInformationResponse =
        EwsAccountInformationResponse.builder().build();

    when(utils.getEwsAccountInformation(ewsUser)).thenReturn(ewsAccountInformationResponse);

    // Action
    ResponseEntity<EwsAccountInformationResponse> responseEntity =
        ewsService.getAccountInformation(ewsUser);

    // Assert
    Assertions.assertNotNull(responseEntity);
    Assertions.assertEquals(httpStatus, responseEntity.getStatusCode());
  }


  @Test
  public void getAccountInformationWithNoEwsUserReturnsHttpStatusBadRequest() {
    // Arrange
    HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    // Action
    ResponseEntity<EwsAccountInformationResponse> responseEntity =
        ewsService.getAccountInformation(null);

    // Assert
    Assertions.assertNotNull(responseEntity);
    Assertions.assertEquals(httpStatus, responseEntity.getStatusCode());
  }

  @Test
  public void getAccountInformationThrowsHttpStatusCodeException() {
    // Arrange
    String ewsUser = "ewsUser";
    HttpStatus httpStatus = HttpStatus.BAD_GATEWAY;

    when(utils.getEwsAccountInformation(ewsUser))
        .thenThrow(new HttpClientErrorException(httpStatus));

    // Action
    ResponseEntity<EwsAccountInformationResponse> responseEntity =
        ewsService.getAccountInformation(ewsUser);

    // Assert
    Assertions.assertNotNull(responseEntity);
    Assertions.assertEquals(httpStatus, responseEntity.getStatusCode());
  }

  @Test
  public void getAccountInformationThrowsNonHttpStatusCodeException() {
    // Arrange
    String ewsUser = "ewsUser";
    HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

    when(utils.getEwsAccountInformation(ewsUser))
        .thenThrow(new UnsupportedOperationException("not-HttpClientErrorException"));

    // Action
    ResponseEntity<EwsAccountInformationResponse> responseEntity =
        ewsService.getAccountInformation(ewsUser);

    // Assert
    Assertions.assertNotNull(responseEntity);
    Assertions.assertEquals(httpStatus, responseEntity.getStatusCode());
  }
}

