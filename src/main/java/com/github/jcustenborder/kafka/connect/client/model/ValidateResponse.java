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

  interface ConfigElement {
    @JsonProperty("definition")
    ConfigDefinition definition();

    @JsonProperty("value")
    ConfigValue value();
  }


  interface ConfigDefinition {
    public enum Type {

    }

    public enum Importance {

    }

    public enum Width {

    }

    @JsonProperty("name")
    String name();

    @JsonProperty("type")
    String type();

    @JsonProperty("required")
    boolean required();

    @JsonProperty("default_value")
    String defaultValue();

    @JsonProperty("importance")
    String importance();

    @JsonProperty("documentation")
    String documentation();

    @JsonProperty("group")
    String group();

    @JsonProperty("order_in_group")
    int orderInGroup();

    @JsonProperty("width")
    String width();

    @JsonProperty("display_name")
    String displayName();

    @JsonProperty("dependents")
    List<String> dependents();
  }

  interface ConfigValue {
    @JsonProperty("name")
    String name();

    @JsonProperty("value")
    String value();

    @JsonProperty("recommended_values")
    List<String> recommendedValues();

    @JsonProperty("errors")
    List<String> errors();

    @JsonProperty("visible")
    boolean visible();
  }
}
