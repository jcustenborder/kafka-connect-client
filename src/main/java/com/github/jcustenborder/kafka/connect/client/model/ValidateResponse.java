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

@Value.Style(jdkOnly = true, visibility = Value.Style.ImplementationVisibility.PACKAGE)
@Value.Immutable
@JsonDeserialize(as = ImmutableValidateResponse.class)
public abstract class ValidateResponse {
  public enum Type {
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

  public enum Importance {
    HIGH,
    LOW,
    MEDIUM
  }

  public enum Width {
    LONG,
    MEDIUM,
    NONE,
    SHORT,
  }

  @JsonProperty("name")
  @Value.Parameter
  public abstract String name();

  @JsonProperty("error_count")
  @Value.Parameter
  public abstract Integer errorCount();

  @JsonProperty("groups")
  @Value.Parameter
  public abstract List<String> groups();

  @JsonProperty("configs")
  @Value.Parameter
  public abstract List<ConfigElement> configs();

  public interface Builder {
    Builder name(String name);

    Builder errorCount(Integer errorCount);

    Builder addGroups(String element);

    Builder addGroups(String... elements);

    Builder groups(Iterable<String> elements);

    Builder addAllGroups(Iterable<String> elements);

    Builder addConfigs(ValidateResponse.ConfigElement element);

    Builder addConfigs(ValidateResponse.ConfigElement... elements);

    Builder configs(Iterable<? extends ValidateResponse.ConfigElement> elements);

    Builder addAllConfigs(Iterable<? extends ValidateResponse.ConfigElement> elements);

    ValidateResponse build();
  }

  public static Builder builder() {
    return ImmutableValidateResponse.builder();
  }

  @Value.Immutable
  @Value.Style(jdkOnly = true, visibility = Value.Style.ImplementationVisibility.PACKAGE)
  @JsonDeserialize(as = ImmutableConfigElement.class)
  public static abstract class ConfigElement {
    @JsonProperty("definition")
    @Value.Parameter
    public abstract ConfigDefinition definition();

    @JsonProperty("value")
    @Value.Parameter
    public abstract ConfigValue value();

    public interface Builder {
      Builder definition(ConfigDefinition definition);

      Builder value(ConfigValue value);

      ConfigElement build();
    }

    public static Builder builder() {
      return ImmutableConfigElement.builder();
    }
  }

  @Value.Immutable
  @Value.Style(jdkOnly = true, visibility = Value.Style.ImplementationVisibility.PACKAGE)
  @JsonDeserialize(as = ImmutableConfigDefinition.class)
  public static abstract class ConfigDefinition {

    @JsonProperty("name")
    @Value.Parameter
    public abstract String name();

    @JsonProperty("type")
    @Value.Parameter
    public abstract Type type();

    @JsonProperty("required")
    @Value.Parameter
    public abstract boolean required();

    @Nullable
    @JsonProperty("default_value")
    @Value.Parameter
    public abstract String defaultValue();

    @JsonProperty("importance")
    @Value.Parameter
    public abstract Importance importance();

    @JsonProperty("documentation")
    @Value.Parameter
    public abstract String documentation();

    @JsonProperty("group")
    @Value.Parameter
    @Nullable
    public abstract String group();

    @Nullable
    @JsonProperty("order_in_group")
    @Value.Parameter
    public abstract Integer orderInGroup();

    @JsonProperty("order")
    @Value.Parameter
    public abstract int order();

    @JsonProperty("width")
    @Value.Parameter
    public abstract Width width();

    @JsonProperty("display_name")
    @Value.Parameter
    public abstract String displayName();

    @JsonProperty("dependents")
    @Value.Parameter
    @Value.Default
    public List<String> dependents() {
      return new ArrayList<>();
    }

    public interface Builder {
      Builder name(String name);

      Builder type(ValidateResponse.Type type);

      Builder required(boolean required);

      Builder defaultValue(@Nullable String defaultValue);

      Builder importance(ValidateResponse.Importance importance);

      Builder documentation(String documentation);

      Builder group(String group);

      Builder orderInGroup(@Nullable Integer orderInGroup);

      Builder order(int order);

      Builder width(ValidateResponse.Width width);

      Builder displayName(String displayName);

      Builder addDependents(String element);

      Builder addDependents(String... elements);

      Builder dependents(Iterable<String> elements);

      Builder addAllDependents(Iterable<String> elements);

      ConfigDefinition build();
    }

    public static Builder builder() {
      return ImmutableConfigDefinition.builder();
    }
  }

  @Value.Immutable
  @Value.Style(jdkOnly = true, visibility = Value.Style.ImplementationVisibility.PACKAGE)
  @JsonDeserialize(as = ImmutableConfigValue.class)
  public static abstract class ConfigValue {
    @JsonProperty("name")
    @Value.Parameter
    public abstract String name();

    @Nullable
    @JsonProperty("value")
    @Value.Parameter
    public abstract String value();

    @JsonProperty("recommended_values")
    @Value.Parameter
    @Value.Default
    public List<String> recommendedValues() {
      return new ArrayList<>();
    }

    @JsonProperty("errors")
    @Value.Parameter
    @Value.Default
    public List<String> errors() {
      return new ArrayList<>();
    }

    @JsonProperty("visible")
    @Value.Parameter
    public abstract boolean visible();

    public interface Builder {
      Builder name(String name);

      Builder value(String value);

      Builder visible(boolean visible);

      Builder addRecommendedValues(String element);

      Builder addRecommendedValues(String... elements);

      Builder recommendedValues(Iterable<String> elements);

      Builder addAllRecommendedValues(Iterable<String> elements);

      Builder addErrors(String element);

      Builder addErrors(String... elements);

      Builder errors(Iterable<String> elements);

      Builder addAllErrors(Iterable<String> elements);
    }

    public static Builder builder() {
      return ImmutableConfigValue.builder();
    }
  }
}
