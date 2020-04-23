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

/**
 * Representation of a connector plugin.
 */
@Value.Style(jdkOnly = true, visibility = Value.Style.ImplementationVisibility.PACKAGE)
@Value.Immutable
@JsonDeserialize(as = ImmutableConnectorPlugin.class)
public abstract class ConnectorPlugin {
  @JsonProperty("class")
  @Value.Parameter
  public abstract String className();

  @JsonProperty("type")
  @Value.Parameter
  public abstract String type();

  @JsonProperty("version")
  @Value.Parameter
  public abstract String version();

  public interface Builder {
    Builder className(String className);

    Builder type(String type);

    Builder version(String version);

    ConnectorPlugin build();
  }

  public static Builder builder() {
    return ImmutableConnectorPlugin.builder();
  }
}
