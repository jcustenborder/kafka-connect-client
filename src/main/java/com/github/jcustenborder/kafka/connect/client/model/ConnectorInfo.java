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
import java.util.List;
import java.util.Map;

@Value.Style(jdkOnly = true, visibility = Value.Style.ImplementationVisibility.PACKAGE)
@Value.Immutable
@JsonDeserialize(as = ImmutableConnectorInfo.class)
public abstract class ConnectorInfo {
  @JsonProperty("name")
  public abstract String name();

  @JsonProperty("config")
  public abstract Map<String, String> config();

  @JsonProperty("tasks")
  public abstract List<TaskInfo> tasks();

  @JsonProperty(value = "type", required = false)
  @Nullable
  public abstract ConnectorType type();

  public interface Builder {
    Builder name(String name);
    Builder putConfig(String key, String value);
    Builder config(Map<String, ? extends String> entries);
    Builder putConfig(Map.Entry<String, ? extends String> entry);
    Builder addTasks(TaskInfo element);
    Builder addTasks(TaskInfo... elements);

    Builder type(ConnectorType type);
    ConnectorInfo build();
  }

  public static Builder builder() {
    return ImmutableConnectorInfo.builder();
  }

}
