package com.github.jcustenborder.kafka.connect.client;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public interface TestCase<A extends TestRequest, B extends TestResponse> {
  @JsonProperty(index = 0)
  List<A> requests();

  @JsonProperty(index = 1)
  List<B> responses();
}
