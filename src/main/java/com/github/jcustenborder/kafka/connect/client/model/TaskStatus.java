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

@Value.Style(jdkOnly = true, visibility = Value.Style.ImplementationVisibility.PACKAGE)
@Value.Immutable
@JsonDeserialize(as = ImmutableTaskStatus.class)
public abstract class TaskStatus extends WorkerStatus {
  @JsonProperty("id")
  @Value.Parameter
  public abstract Integer id();

  public interface Builder {
    Builder id(Integer id);
    Builder state(State state);
    Builder workerID(String workerID);
    Builder trace(String trace);
    TaskStatus build();
  }

  public static Builder builder() {
    return ImmutableTaskStatus.builder();
  }
}
