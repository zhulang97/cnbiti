package com.cnbjti.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class JsonCodec {

  private final ObjectMapper objectMapper;

  public JsonCodec(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public String write(Object value) {
    try {
      return objectMapper.writeValueAsString(value);
    } catch (JsonProcessingException ex) {
      throw new IllegalStateException("Unable to serialize catalog payload", ex);
    }
  }

  public <T> T read(String json, Class<T> type) {
    try {
      return objectMapper.readValue(json, type);
    } catch (JsonProcessingException ex) {
      throw new IllegalStateException("Unable to parse catalog payload", ex);
    }
  }
}
