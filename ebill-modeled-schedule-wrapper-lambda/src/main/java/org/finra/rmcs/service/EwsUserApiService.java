package org.finra.rmcs.service;

import org.finra.rmcs.dto.EwsApiResponse;
import org.finra.rmcs.exception.TokenException;


public interface EwsUserApiService {

  EwsApiResponse getEWSUser(String userId) throws TokenException;

}
