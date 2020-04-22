package com.github.jcustenborder.kafka.connect.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;


@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableResponseMetadata.class)
public interface ResponseMetadata extends Headers {
  @JsonProperty("statusCode")
  Integer statusCode();
}
