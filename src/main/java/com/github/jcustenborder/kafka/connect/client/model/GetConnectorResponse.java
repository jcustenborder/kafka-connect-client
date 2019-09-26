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
import java.util.Map;

public class GetConnectorResponse {
  @Key("name")
  String name;

  @Key("config")
  Map<String, String> config;

  @Key("tasks")
  List<CreateOrUpdateConnectorResponse.Task> tasks;

  @Key("type")
  ConnectorType type;

  public String name() {
    return this.name;
  }

  public Map<String, String> config() {
    return this.config;
  }

  public List<CreateOrUpdateConnectorResponse.Task> tasks() {
    return this.tasks;
  }

  public ConnectorType type() {
    return this.type;
  }
}
