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
package com.github.jcustenborder.kafka.connect.client.model;

import com.google.api.client.util.Key;

import java.util.List;

public class ValidateResponse {
  @Key("name")
  String name;
  @Key("error_count")
  Integer errorCount;
  @Key("groups")
  List<String> groups;
  @Key("configs")
  List<ConfigElement> configs;

  public String name() {
    return this.name;
  }

  public Integer errorCount() {
    return this.errorCount;
  }

  public List<String> groups() {
    return this.groups;
  }

  public List<ConfigElement> configs() {
    return this.configs;
  }

  public static class ConfigElement {
    @Key("definition")
    ConfigDefinition definition;
    @Key("value")
    ConfigValue value;

    public ConfigDefinition definition() {
      return this.definition;
    }

    public ConfigValue value() {
      return this.value;
    }
  }


  public static class ConfigDefinition {
    public enum Type {

    }

    public enum Importance {

    }

    public enum Width {

    }

    @Key("name")
    String name;
    @Key("type")
    String type;
    @Key("required")
    boolean required;
    @Key("default_value")
    String defaultValue;
    @Key("importance")
    String importance;
    @Key("documentation")
    String documentation;
    @Key("group")
    String group;
    @Key("order_in_group")
    int orderInGroup;
    @Key("width")
    String width;
    @Key("display_name")
    String displayName;
    @Key("dependents")
    List<String> dependents;

    public String name() {
      return this.name;
    }

    public String type() {
      return this.type;
    }

    public boolean required() {
      return this.required;
    }

    public String defaultValue() {
      return this.defaultValue;
    }

    public String importance() {
      return this.importance;
    }

    public String documentation() {
      return this.documentation;
    }

    public String group() {
      return this.group;
    }

    public int orderInGroup() {
      return this.orderInGroup;
    }

    public String width() {
      return this.width;
    }

    public String displayName() {
      return this.displayName;
    }

    public List<String> dependents() {
      return this.dependents;
    }
  }

  public static class ConfigValue {
    @Key("name") String name;
    @Key("value") String value;
    @Key("recommended_values") List<String> recommendedValues;
    @Key("errors") List<String> errors;
    @Key("visible") boolean visible;

    public String name() {
      return this.name;
    }

    public String value() {
      return this.value;
    }

    public List<String> recommendedValues() {
      return this.recommendedValues;
    }

    public List<String> errors() {
      return this.errors;
    }

    public boolean visible() {
      return this.visible;
    }
  }
}
