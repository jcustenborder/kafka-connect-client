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

import java.util.Map;

@Value.Style(jdkOnly = true, visibility = Value.Style.ImplementationVisibility.PACKAGE)
@Value.Immutable
@JsonDeserialize(as = ImmutableTaskConfig.class)
public abstract class TaskConfig {
  public static Builder builder() {
    return ImmutableTaskConfig.builder();
  }

  @JsonProperty("id")
  @Value.Parameter
  public abstract TaskInfo id();

  @JsonProperty("config")
  @Value.Parameter
  public abstract Map<String, String> config();

  public interface Builder {
    Builder id(TaskInfo id);

    Builder putConfig(String key, String value);

    Builder putConfig(Map.Entry<String, ? extends String> entry);

    Builder config(Map<String, ? extends String> entries);

    Builder putAllConfig(Map<String, ? extends String> entries);

    TaskConfig build();
  }
}
