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
package com.github.jcustenborder.kafka.connect.client;

import com.github.jcustenborder.kafka.connect.client.model.ConnectorInfo;
import com.github.jcustenborder.kafka.connect.client.model.ConnectorPlugin;
import com.github.jcustenborder.kafka.connect.client.model.ConnectorStatus;
import com.github.jcustenborder.kafka.connect.client.model.CreateConnectorRequest;
import com.github.jcustenborder.kafka.connect.client.model.CreateConnectorResponse;
import com.github.jcustenborder.kafka.connect.client.model.ServerInfo;
import com.github.jcustenborder.kafka.connect.client.model.TaskConfig;
import com.github.jcustenborder.kafka.connect.client.model.TaskStatus;
import com.github.jcustenborder.kafka.connect.client.model.ValidateResponse;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface AsyncKafkaConnectClient extends AutoCloseable {
  /**
   * Method is used to list the connectors.
   *
   * @return list of connector names.
   */
  CompletableFuture<List<String>> connectorsAsync();

  default CompletableFuture<CreateConnectorResponse> createConnectorAsync(String name, Map<String, String> config) {
    return createConnectorAsync(
        CreateConnectorRequest.builder()
            .name(name)
            .putAllConfig(config)
            .build()
    );
  }

  CompletableFuture<CreateConnectorResponse> createConnectorAsync(CreateConnectorRequest request);

  /**
   * Method is used to create a connector.
   *
   * @param name   name of the connector.
   * @param config config for the connector.
   * @return Information about the created or updated connector.
   */
  CompletableFuture<ConnectorInfo> createOrUpdateAsync(String name, Map<String, String> config);

  /**
   * Method is used to return information about the connector.
   *
   * @param name name of the connector.
   * @return Information about the requested connector.
   */
  CompletableFuture<ConnectorInfo> infoAsync(String name);

  /**
   * Method is used to retrieve the configuration for a connector.
   *
   * @param name name of the connector.
   * @return map of the configuration.
   */
  CompletableFuture<Map<String, String>> configAsync(String name);


  /**
   * Method is used to delete a connector
   *
   * @param name name of the connector.
   */
  CompletableFuture<Void> deleteAsync(String name);

  /**
   * Method is used to restart a connector
   *
   * @param name name of the connector.
   */
  CompletableFuture<Void> restartAsync(String name);

  /**
   * Method is used to pause a connector
   *
   * @param name name of the connector.
   */
  CompletableFuture<Void> pauseAsync(String name);

  /**
   * Method is used to pause a connector
   *
   * @param name name of the connector.
   */
  CompletableFuture<Void> resumeAsync(String name);

  /**
   * Method is used to return the status for a connector.
   *
   * @param name name of the connector.
   * @return status of the requested connector
   */
  CompletableFuture<ConnectorStatus> statusAsync(String name);

  /**
   * Method is  used to return state of a task.
   *
   * @param name   name of the connector.
   * @param taskId taskId  to restart
   * @return status of the requested task
   */
  CompletableFuture<TaskStatus> statusAsync(String name, int taskId);

  /**
   * Method is used to restart a single task.
   *
   * @param name   name of the connector.
   * @param taskId taskId to restart
   */
  CompletableFuture<Void> restartAsync(String name, int taskId);

  /**
   * Method is used to return the available connector plugins.
   *
   * @return list of connector plugins available on the worker.
   */
  CompletableFuture<List<ConnectorPlugin>> connectorPluginsAsync();

  /**
   * Method is used to validate the configuration of a connector.
   *
   * @param name   name of the connector plugin to test.
   * @param config config to test
   * @return Validation response
   */
  CompletableFuture<ValidateResponse> validateAsync(String name, Map<String, String> config);

  /**
   * Method is used to return the information about the connect worker.
   *
   * @return returns metadata about the connect worker.
   */
  CompletableFuture<ServerInfo> serverInfoAsync();

  CompletableFuture<List<TaskConfig>> taskConfigsAsync(String name);
}
