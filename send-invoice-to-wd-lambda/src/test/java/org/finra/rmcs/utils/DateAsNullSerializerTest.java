package org.finra.rmcs.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.filter.FilteringGeneratorDelegate;
import com.fasterxml.jackson.core.filter.TokenFilter;
import com.fasterxml.jackson.core.util.JsonGeneratorDelegate;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import java.io.IOException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
public class DateAsNullSerializerTest {

  /**
   * Method under test:
   * {@link DateAsNullSerializer#serialize(LocalDateTime, JsonGenerator, SerializerProvider)}
   */
  @Test
  public void testSerialize() throws IOException {
    DateAsNullSerializer dateAsNullSerializer = new DateAsNullSerializer();
    LocalDateTime value = LocalDateTime.of(1, 1, 1, 1, 1);
    JsonGenerator jsonGenerator = mock(JsonGenerator.class);
    doNothing().when(jsonGenerator).writeString((String) any());
    JsonGeneratorDelegate gen =
        new JsonGeneratorDelegate(
            new JsonGeneratorDelegate(
                new JsonGeneratorDelegate(
                    new JsonGeneratorDelegate(new JsonGeneratorDelegate(jsonGenerator, true), true),
                    true),
                true),
            true);

    dateAsNullSerializer.serialize(value, gen, new DefaultSerializerProvider.Impl());
    verify(jsonGenerator).writeString((String) any());
  }

  /**
   * Method under test:
   * {@link DateAsNullSerializer#serialize(LocalDateTime, JsonGenerator, SerializerProvider)}
   */
  @Test
  public void testSerialize3() throws IOException {
    DateAsNullSerializer dateAsNullSerializer = new DateAsNullSerializer();
    LocalDateTime value = LocalDateTime.of(1, 1, 1, 1, 1);
    JsonGenerator jsonGenerator = mock(JsonGenerator.class);
    doNothing().when(jsonGenerator).writeString((String) any());
    JsonGeneratorDelegate d =
        new JsonGeneratorDelegate(
            new JsonGeneratorDelegate(
                new JsonGeneratorDelegate(new JsonGeneratorDelegate(jsonGenerator, true), true),
                true),
            true);

    TokenFilter tokenFilter = mock(TokenFilter.class);
    when(tokenFilter.includeString((String) any())).thenReturn(true);
    TokenFilter tokenFilter1 = mock(TokenFilter.class);
    when(tokenFilter1.includeRootValue(anyInt())).thenReturn(tokenFilter);
    JsonGeneratorDelegate jsonGeneratorDelegate =
        new JsonGeneratorDelegate(
            new JsonGeneratorDelegate(
                new JsonGeneratorDelegate(
                    new JsonGeneratorDelegate(
                        new FilteringGeneratorDelegate(
                            d, tokenFilter1, TokenFilter.Inclusion.ONLY_INCLUDE_ALL, true),
                        true),
                    true),
                true),
            true);

    dateAsNullSerializer.serialize(
        value, jsonGeneratorDelegate, new DefaultSerializerProvider.Impl());
    verify(jsonGenerator).writeString((String) any());
    verify(tokenFilter1).includeRootValue(anyInt());
    verify(tokenFilter).includeString((String) any());
    assertEquals(1, jsonGeneratorDelegate.getOutputContext().getEntryCount());
  }
}
