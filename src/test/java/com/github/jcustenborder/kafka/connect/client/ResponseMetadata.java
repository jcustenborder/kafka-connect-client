/**
 * Copyright © 2019 Jeremy Custenborder (jcustenborder@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
