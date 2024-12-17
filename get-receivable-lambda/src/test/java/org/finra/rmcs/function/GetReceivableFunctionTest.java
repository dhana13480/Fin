package org.finra.rmcs.function;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import org.finra.rmcs.TestFileReaderUtil;
import org.finra.rmcs.dto.GetReceivablesRequest;
import org.finra.rmcs.entity.ReceivableEntity;
import org.finra.rmcs.entity.Receivables;
import org.finra.rmcs.service.ReceivableService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.util.ReflectionTestUtils;

@SpringJUnitConfig
public class GetReceivableFunctionTest {

  static Map<String, Object> request;
  static GetReceivablesRequest getReceivablesRequest = null;
  static Receivables receivables = null;
  @Mock
  ReceivableService receivableService;
  @Spy
  ObjectMapper objectMapper = new ObjectMapper();
  @InjectMocks
  GetReceivableFunction getReceivableFunction;

  @BeforeAll
  public static void initReceivables() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    request = mapper.readerFor(Object.class)
        .readValue(TestFileReaderUtil.getResourceContent("GetReceivablesRequest.json"));

    // Parse request
    getReceivablesRequest =
        mapper.readValue(mapper.writeValueAsString(request), GetReceivablesRequest.class);

    receivables = new Receivables();
    receivables.setReceivables(getReceivablesRequest.getInvoiceIds().stream().map(i -> {
      ReceivableEntity receivable = new ReceivableEntity();
      receivable.setInvoiceId(i);
      return receivable;
    }).collect(Collectors.toList()));
  }

  @BeforeEach
  public void init() {
    ReflectionTestUtils.setField(getReceivableFunction, "enableDecodeToken", false);
  }

  @Test
  public void applySuccessTest() throws Exception {
    when(receivableService.findValidReceivablesByInvoiceIds(anyList()))
        .thenReturn(receivables.getReceivables());
    ResponseEntity<Map<String, Object>> response = getReceivableFunction.apply(request);
    Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
  }

}
