/**
 * Copyright © 2019 Jeremy Custenborder (jcustenborder@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jcustenborder.kafka.connect.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Value.Style(jdkOnly = true)
@Value.Immutable
@JsonDeserialize(as = ImmutableValidateResponse.class)
public interface ValidateResponse {
  @JsonProperty("name")
  String name();

  @JsonProperty("error_count")
  Integer errorCount();

  @JsonProperty("groups")
  List<String> groups();

  @JsonProperty("configs")
  List<ConfigElement> configs();

  @Value.Immutable
  @Value.Style(jdkOnly = true)
  @JsonDeserialize(as = ImmutableConfigElement.class)
  interface ConfigElement {
    @JsonProperty("definition")
    ConfigDefinition definition();

    @JsonProperty("value")
    ConfigValue value();
  }

  @Value.Immutable
  @Value.Style(jdkOnly = true)
  @JsonDeserialize(as = ImmutableConfigDefinition.class)
  interface ConfigDefinition {
    enum Type {
      BOOLEAN,
      CLASS,
      DOUBLE,
      INT,
      LIST,
      LONG,
      PASSWORD,
      SHORT,
      STRING
    }

    enum Importance {
      HIGH,
      LOW,
      MEDIUM
    }

    enum Width {
      LONG,
      MEDIUM,
      NONE,
      SHORT,
    }

    @JsonProperty("name")
    String name();

    @JsonProperty("type")
    Type type();

    @JsonProperty("required")
    boolean required();

    @Nullable
    @JsonProperty("default_value")
    String defaultValue();

    @JsonProperty("importance")
    Importance importance();

    @JsonProperty("documentation")
    String documentation();

    @JsonProperty("group")
    String group();

    @Nullable
    @JsonProperty("order_in_group")
    Integer orderInGroup();

    @JsonProperty("order")
    int order();

    @JsonProperty("width")
    Width width();

    @JsonProperty("display_name")
    String displayName();

    @JsonProperty("dependents")
    default List<String> dependents() {
      return new ArrayList<>();
    }
  }

  @Value.Immutable
  @Value.Style(jdkOnly = true)
  @JsonDeserialize(as = ImmutableConfigValue.class)
  interface ConfigValue {
    @JsonProperty("name")
    String name();

    @Nullable
    @JsonProperty("value")
    String value();

    @JsonProperty("recommended_values")
    default List<String> recommendedValues() {
      return new ArrayList<>();
    }

    @JsonProperty("errors")
    default List<String> errors() {
      return new ArrayList<>();
    }

    @JsonProperty("visible")
    boolean visible();
  }
}
