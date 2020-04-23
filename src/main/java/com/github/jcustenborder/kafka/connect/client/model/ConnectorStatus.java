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

import javax.annotation.Nullable;
import java.util.List;

@Value.Style(jdkOnly = true, visibility = Value.Style.ImplementationVisibility.PACKAGE)
@Value.Immutable
@JsonDeserialize(as = ImmutableConnectorStatus.class)
public abstract class ConnectorStatus {
  @JsonProperty("name")
  @Value.Parameter
  public abstract String name();

  @JsonProperty("connector")
  @Value.Parameter
  public abstract ConnectorState connector();

  @JsonProperty("tasks")
  @Value.Parameter
  public abstract List<TaskStatus> tasks();

  @Nullable
  @JsonProperty("type")
  @Value.Parameter
  public abstract String type();

  public interface Builder {
    Builder name(String name);
    Builder connector(ConnectorState connector);
    Builder addTasks(TaskStatus element);
    Builder addTasks(TaskStatus... elements);
    Builder tasks(Iterable<? extends TaskStatus> elements);
    Builder type(String type);
    ConnectorStatus build();
  }

  public static Builder builder() {
    return ImmutableConnectorStatus.builder();
  }
}
