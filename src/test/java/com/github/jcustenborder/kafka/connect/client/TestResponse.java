package com.github.jcustenborder.kafka.connect.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jcustenborder.kafka.connect.client.model.ErrorResponse;

import javax.annotation.Nullable;

public interface TestResponse<T> {
  @JsonProperty("metadata")
  ResponseMetadata metadata();

  @Nullable
  @JsonProperty("error")
  ErrorResponse error();

  @Nullable
  @JsonProperty("body")
  T body();
}
