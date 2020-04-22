package com.github.jcustenborder.kafka.connect.client;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;

public interface TestRequest<T> {
  @JsonProperty("metadata")
  RequestMetadata metadata();

  @Nullable
  @JsonProperty(value = "body", index = 3)
  T body();
}
