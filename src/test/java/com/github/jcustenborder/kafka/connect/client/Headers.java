package com.github.jcustenborder.kafka.connect.client;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedHashMap;
import java.util.Map;

public interface Headers {
  @JsonProperty(value = "headers", index = 0)
  default Map<String, String> headers() {
    Map<String, String> headers = new LinkedHashMap<>();
    headers.put("Accept", "application/json");
    return headers;
  }
}
