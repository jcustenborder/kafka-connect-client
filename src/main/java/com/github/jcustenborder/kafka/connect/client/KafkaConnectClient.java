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
import org.immutables.value.Value;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Interface represents the available rest methods for Kafka Connect.
 */
public interface KafkaConnectClient extends AutoCloseable {
  /**
   * Method is used to list the connectors.
   *
   * @return list of connector names.
   * @throws IOException exception is thrown by the api.
   */
  List<String> connectors() throws IOException;

  default CreateConnectorResponse createConnector(String name, Map<String, String> config) throws IOException {
    return createConnector(
        CreateConnectorRequest.builder()
            .name(name)
            .putAllConfig(config)
            .build()
    );
  }

  CreateConnectorResponse createConnector(CreateConnectorRequest request) throws IOException;

  /**
   * Method is used to create a connector.
   *
   * @param name   name of the connector.
   * @param config config for the connector.
   * @return Information about the created or updated connector.
   * @throws IllegalArgumentException exception is thrown in 'connector.class' or 'tasks.max' is not set.
   * @throws IOException              exception is thrown by the api.
   */
  ConnectorInfo createOrUpdate(String name, Map<String, String> config) throws IOException;

  /**
   * Method is used to return information about the connector.
   *
   * @param name name of the connector.
   * @return Information about the requested connector.
   * @throws IOException exception is thrown by the api.
   */
  ConnectorInfo info(String name) throws IOException;

  /**
   * Method is used to retrieve the configuration for a connector.
   *
   * @param name name of the connector.
   * @return map of the configuration.
   * @throws IOException exception is thrown by the api.
   */
  Map<String, String> config(String name) throws IOException;


  /**
   * Method is used to delete a connector
   *
   * @param name name of the connector.
   * @throws IOException exception is thrown by the api.
   */
  void delete(String name) throws IOException;

  /**
   * Method is used to restart a connector
   *
   * @param name name of the connector.
   * @throws IOException exception is thrown by the api.
   */
  void restart(String name) throws IOException;

  /**
   * Method is used to pause a connector
   *
   * @param name name of the connector.
   * @throws IOException exception is thrown by the api.
   */
  void pause(String name) throws IOException;

  /**
   * Method is used to pause a connector
   *
   * @param name name of the connector.
   * @throws IOException exception is thrown by the api.
   */
  void resume(String name) throws IOException;

  /**
   * Method is used to return the status for a connector.
   *
   * @param name name of the connector.
   * @return status of the requested connector
   * @throws IOException exception is thrown by the api.
   */
  ConnectorStatus status(String name) throws IOException;

  /**
   * Method is  used to return state of a task.
   *
   * @param name   name of the connector.
   * @param taskId taskId  to restart
   * @return status of the requested task
   * @throws IOException exception is thrown by the api.
   */
  TaskStatus status(String name, int taskId) throws IOException;

  /**
   * Method is used to restart a single task.
   *
   * @param name   name of the connector.
   * @param taskId taskId to restart
   * @throws IOException exception is thrown by the api.
   */
  void restart(String name, int taskId) throws IOException;

  /**
   * Method is used to return the available connector plugins.
   *
   * @return list of connector plugins available on the worker.
   * @throws IOException exception is thrown by the api.
   */
  List<ConnectorPlugin> connectorPlugins() throws IOException;

  /**
   * Method is used to validate the configuration of a connector.
   *
   * @param name   name of the connector plugin to test.
   * @param config config to test
   * @return Validation response
   * @throws IOException exception is thrown by the api.
   */
  ValidateResponse validate(String name, Map<String, String> config) throws IOException;

  /**
   * Method is used to return the information about the connect worker.
   *
   * @return returns metadata about the connect worker.
   * @throws IOException exception is thrown by the api.
   */
  ServerInfo serverInfo() throws IOException;


  /**
   * @param name name of the connector
   * @return
   */
  List<TaskConfig> taskConfigs(String name) throws IOException;

  @Value.Style(jdkOnly = true, visibility = Value.Style.ImplementationVisibility.PACKAGE)
  @Value.Immutable
  class Settings extends AbstractSettings {
    public interface Builder extends ClientBuilder<Builder> {
      Settings build();
      default KafkaConnectClient createClient() {
        Settings settings = this.build();
        return new KafkaConnectClientImpl(settings);
      }
    }
  }

  static Settings.Builder builder() {
    return ImmutableSettings.builder();
  }
}
