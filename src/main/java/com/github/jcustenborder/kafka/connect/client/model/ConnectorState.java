package com.github.jcustenborder.kafka.connect.client.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Style(jdkOnly = true)
@Value.Immutable
@JsonDeserialize(as = ImmutableConnectorState.class)
public interface ConnectorState extends WorkerStatus {

}
