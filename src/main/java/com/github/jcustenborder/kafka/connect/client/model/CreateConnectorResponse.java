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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.List;
import java.util.Map;

@Value.Style(jdkOnly = true, visibility = Value.Style.ImplementationVisibility.PACKAGE)
@Value.Immutable
@JsonDeserialize(as = ImmutableCreateConnectorResponse.class)
public abstract class CreateConnectorResponse {
  /**
   * name (string) – Name of the created connector
   * config (map) – Configuration parameters for the connector.
   * tasks (array) – List of active tasks generated by the connector
   * tasks[i].connector (string) – The name of the connector the task belongs to
   * tasks[i].task (int) – Task ID within the connector.
   */
  @JsonProperty("name")
  @Value.Parameter
  public abstract String name();

  @JsonProperty("config")
  @Value.Parameter
  public abstract Map<String, String> config();

  @JsonProperty("tasks")
  @Value.Parameter
  public abstract List<TaskInfo> tasks();

  public interface Builder {
    Builder name(String name);
    Builder putConfig(String key, String value);
    Builder config(Map<String, ? extends String> entries);
    Builder putConfig(Map.Entry<String, ? extends String> entry);
    Builder addTasks(TaskInfo element);
    Builder addTasks(TaskInfo... elements);
    Builder tasks(Iterable<? extends TaskInfo> elements);
    CreateConnectorResponse build();
  }

  public static Builder builder() {
    return CreateConnectorResponse.builder();
  }
}
