package com.github.jcustenborder.kafka.connect.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;


@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableRequestMetadata.class)
public interface RequestMetadata  {
  @JsonProperty(value = "headers", index = 0)
  default Map<String, String> headers() {
    Map<String, String> headers = new LinkedHashMap<>();
    headers.put("Accept", "application/json");
    return headers;
  }

  @Nullable
  @JsonProperty(value = "method", index = 1)
  String method();

  @Nullable
  @JsonProperty(value = "path", index = 2)
  String path();
}
