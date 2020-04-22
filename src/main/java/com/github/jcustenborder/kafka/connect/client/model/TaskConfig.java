package com.github.jcustenborder.kafka.connect.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.Map;

@Value.Style(jdkOnly = true)
@Value.Immutable
@JsonDeserialize(as = ImmutableTaskConfig.class)
public interface TaskConfig {
  @JsonProperty("id")
  TaskInfo id();
  @JsonProperty("config")
  Map<String, String> config();
}
