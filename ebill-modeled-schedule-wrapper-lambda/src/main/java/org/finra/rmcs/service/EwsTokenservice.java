package org.finra.rmcs.service;

import org.finra.rmcs.exception.TokenException;


public interface EwsTokenservice {

  String getEwsToken() throws TokenException;

}
