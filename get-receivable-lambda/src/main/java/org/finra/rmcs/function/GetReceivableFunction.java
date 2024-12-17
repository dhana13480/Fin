package org.finra.rmcs.function;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.dto.GetReceivablesRequest;
import org.finra.rmcs.dto.TokenContext;
import org.finra.rmcs.entity.ReceivableEntity;
import org.finra.rmcs.service.ReceivableService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import com.amazonaws.services.securitytoken.model.ExpiredTokenException;
import com.auth0.jwk.GuavaCachedJwkProvider;
import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;


@Component
@RequiredArgsConstructor
@Slf4j
public class GetReceivableFunction
    implements Function<Map<String, Object>, ResponseEntity<Map<String, Object>>> {
  @NonNull
  private final ReceivableService receivableService;
  private final ObjectMapper objectMapper;

  @Value("${fip.token.enable}")
  private boolean enableDecodeToken;

  @SneakyThrows
  @Override
  public ResponseEntity<Map<String, Object>> apply(Map<String, Object> requestEvent) {
    String correlationId = UUID.randomUUID().toString();
    String methodName =
        Constants.CLASS
            + this.getClass().getSimpleName()
            + " "
            + Constants.METHOD
            + Thread.currentThread().getStackTrace()[1].getMethodName()
            + " "
            + Constants.CORRELATION_ID
            + correlationId;
    log.info("{} message: method entry", methodName);
    log.info("{} message, Incoming request: {}", methodName, requestEvent);

    boolean dryRun =
        Boolean.parseBoolean(
            ((Map<String, Object>)
                requestEvent.getOrDefault(Constants.BODY_JSON, Collections.emptyMap()))
                .getOrDefault(Constants.DRY_RUN, Constants.FALSE)
                .toString());
    log.info("{} message, dryRun: {}", methodName, dryRun);
    if (dryRun) {
      log.info("{} message, dry run mode", methodName);
      Map<String, Object> response = new HashMap<>();
      response.put(Constants.MESSAGE_NODE, Constants.DRY_RUN_PROCESSED_SUCCESSFULLY);
      return new ResponseEntity<>(response, null, HttpStatus.OK);
    }

    GetReceivablesRequest createPmtReq =null;
    if (requestEvent.get(Constants.BODY_JSON) != null) {
      log.info("Request coming from API gateway...");
      createPmtReq = objectMapper.convertValue(requestEvent.get(Constants.BODY_JSON), GetReceivablesRequest.class);
    } 
        
    TokenContext ctx = objectMapper.convertValue(requestEvent.get(Constants.TOKEN_CONTEXT), TokenContext.class);
    
    String jwtToken = ctx != null ? ctx.getIdToken() : StringUtils.EMPTY;
    
    // validate jwt
    ResponseEntity<Map<String, Object>> respEnt = validateJwtToken(jwtToken, createPmtReq.getCorrelationId());
    if (respEnt != null)
      return respEnt;
    
    log.info("Validating request...");

    // validate request
    respEnt = validateRequest(createPmtReq, requestEvent);
    if (respEnt != null)
      return respEnt;

    Map<String, Object> response = new HashMap<>();

    List<ReceivableEntity> receivables = new ArrayList<>();
    log.info("Retreving receivables...");
    try {
      receivables =
          receivableService.findValidReceivablesByInvoiceIds(createPmtReq.getInvoiceIds());
    } catch (Exception e) {
      log.error("Get-Receivable-Lambda, retrieving receivalbles failed, error:{}", e);
      response.put(Constants.MESSAGE_NODE, Collections
          .singletonList(String.format("Error retrieving receivables, error: %s", e.getMessage())));
      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    log.info("Return receivalbes, size: {}", receivables.size());
    response.put("receivables", receivables);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  private ResponseEntity<Map<String, Object>> validateRequest(GetReceivablesRequest createPmtReq,
      Map<String, Object> requestEvent) {
    Map<String, Object> response = new HashMap<>();
    List<String> errMsgs = new ArrayList<>();

    // Request validation
    if (createPmtReq == null) {
      log.error("Invalid request, parsed request is null");
      errMsgs.add(String.format("Invalid request: %s", requestEvent));
    } else {
      if(createPmtReq.getIds() == null || createPmtReq.getIds().size() == 0) {
        errMsgs.add("Invoice Id(s) can not be empty.");
      }
      
      if(StringUtils.isBlank(createPmtReq.getCorrelationId())){
        errMsgs.add("Correlation Id is required.");
      } else {    
        try {
          // Correlation Id is valid uuid string
          createPmtReq.setTransmissionId(UUID.fromString(createPmtReq.getCorrelationId()));
        } catch (IllegalArgumentException e) {
          errMsgs.add(String.format("Invalid correlation Id: %s", createPmtReq.getCorrelationId()));
        }
      }
    }
    if (!errMsgs.isEmpty()) {
      log.info("Get-Receivable-Lambda, request validation false, error messages: {}", errMsgs);
      response.put(Constants.MESSAGE_NODE, errMsgs);
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    return null;
  }

  private ResponseEntity<Map<String, Object>> validateJwtToken(String jwtToken, String correlationId) {
    Map<String, Object> response = new HashMap<>();
    List<String> errMsgs = new ArrayList<>();
    
    log.info("enableDecodeToken is ", enableDecodeToken);
    
    // JWT Token validation
    if (enableDecodeToken) {
      try {
        decodeAndValidateToken(jwtToken, correlationId);
      } catch (ExpiredTokenException e) {
        log.error("{} message: JWT token expired {} ");
        errMsgs.add(String.format("Error validating jwt token, error: %s", e.getMessage()));
        response.put("message", errMsgs);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
      }
    }

    return null;
  }

  @SneakyThrows
  public void decodeAndValidateToken(String jwtToken, String correlationId) {
    String methodName = Constants.CLASS + this.getClass().getSimpleName() + " " + Constants.METHOD
        + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + Constants.CORRELATION_ID
        + correlationId;

    String tokenJwkProviderUrl = System.getenv(Constants.FIP_JWKS_ENDPOINT);

    URL url = new URL(tokenJwkProviderUrl);
    DecodedJWT decodedJwt = JWT.decode(jwtToken);
    JwkProvider provider =
        new GuavaCachedJwkProvider(new UrlJwkProvider(url), 10, 10, TimeUnit.MINUTES);
    String tokenKey = decodedJwt.getKeyId();
    log.info("{} message: KeyId from JWT  {}", methodName, tokenKey);

    // Check expiration
    if (decodedJwt.getExpiresAt().before(Calendar.getInstance().getTime())) {
      log.info("{} message: Token is expired", methodName);
      throw new ExpiredTokenException(Constants.EXPIRED_TOKEN_MSG);
    }
    Jwk jwk = provider.get(tokenKey);
    log.info("{} message: Got key id from provider {}", methodName, jwk);

    Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
    log.info("{} message: Algorithm is configured", methodName);

    Verification verifier = JWT.require(algorithm);
    verifier.build().verify(decodedJwt);
    log.info("{} message: Verification is done", methodName);

    String tokenPayload = decodedJwt.getPayload();
    log.info("{} message: The the tokenPayload string is {}", methodName, tokenPayload);

    String decodedPayload = new String(Base64.getUrlDecoder().decode(tokenPayload));
    log.info("{} message: The the decodedPayload string is {}", methodName, decodedPayload);
  }
}
