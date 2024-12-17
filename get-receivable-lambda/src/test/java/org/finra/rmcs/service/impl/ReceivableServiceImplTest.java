package org.finra.rmcs.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.finra.rmcs.TestFileReaderUtil;
import org.finra.rmcs.dto.GetReceivablesRequest;
import org.finra.rmcs.entity.ReceivableEntity;
import org.finra.rmcs.entity.Receivables;
import org.finra.rmcs.repo.ReceivableRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
public class ReceivableServiceImplTest {

  static Map<String, Object> request;
  static GetReceivablesRequest getReceivablesRequest = null;
  static Receivables receivables = null;
  @Mock
  ReceivableRepo receivableRepo;
  @InjectMocks
  ReceivableServiceImpl receivableService;

  @BeforeEach
  public void initReceivables() throws IOException {
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

  @Test
  public void test_validateReceivables_success() throws Exception {
    when(receivableRepo.findValidReceivablesByInvoiceIds(anyList(), any(Integer.class),
        any(Integer.class))).thenReturn(receivables.getReceivables());
    List<ReceivableEntity> list =
        receivableService.findValidReceivablesByInvoiceIds(getReceivablesRequest.getInvoiceIds());
    Assertions.assertEquals(list, receivables.getReceivables());
  }

}
