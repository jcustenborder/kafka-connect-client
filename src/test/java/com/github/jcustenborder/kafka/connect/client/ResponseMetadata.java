package com.github.jcustenborder.kafka.connect.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.LinkedHashMap;
import java.util.Map;


@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableResponseMetadata.class)
public interface ResponseMetadata {
  @JsonProperty(value = "headers", index = 0)
  default Map<String, String> headers() {
    Map<String, String> headers = new LinkedHashMap<>();
    headers.put("Content-Type", "application/json");
    return headers;
  }

  @JsonProperty("statusCode")
  int statusCode();

  @JsonIgnore
  default boolean isSuccessful() {
    return statusCode() >= 200 && statusCode() <= 400;
  }
}
