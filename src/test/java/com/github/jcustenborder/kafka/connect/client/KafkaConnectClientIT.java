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

import com.github.jcustenborder.docker.junit5.Compose;
import com.github.jcustenborder.docker.junit5.Port;
import com.github.jcustenborder.kafka.connect.client.model.ConnectorInfo;
import com.github.jcustenborder.kafka.connect.client.model.ConnectorPlugin;
import com.github.jcustenborder.kafka.connect.client.model.ConnectorStatus;
import com.github.jcustenborder.kafka.connect.client.model.TaskStatus;
import com.github.jcustenborder.kafka.connect.client.model.WorkerStatus;
import com.github.jcustenborder.kafka.connect.client.model.ConnectorType;
import com.github.jcustenborder.kafka.connect.client.model.ServerInfo;
import com.github.jcustenborder.kafka.connect.client.model.State;
import com.github.jcustenborder.kafka.connect.client.model.ValidateResponse;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;


@Disabled
@Compose(dockerComposePath = "src/test/resources/docker-compose.yml", clusterHealthCheck = KafkaConnectHealthCheck.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class KafkaConnectClientIT {
  private static final Logger log = LoggerFactory.getLogger(KafkaConnectClientIT.class);

  KafkaConnectClient client;


  @BeforeEach
  public void before(@Port(container = "connect", internalPort = 8083) InetSocketAddress address) {
    log.info("before() - Configuring client factory to {}", address);
    KafkaConnectClientFactory clientFactory;
    clientFactory = new KafkaConnectClientFactory();
    clientFactory.host(address.getHostString());
    clientFactory.port(address.getPort());
    this.client = clientFactory.createClient();
  }

  void ensureNoConnectors() throws IOException {
    List<String> connectors = this.client.connectors();
    assertNotNull(connectors);
    assertTrue(connectors.isEmpty());
  }

  @Order(0)
  @Test
  public void startShouldBeNoConnectors() throws IOException {
    ensureNoConnectors();
  }

  @Order(1)
  @Test
  public void connectorPlugins() throws IOException {
    List<ConnectorPlugin> connectorPlugins = client.connectorPlugins();
    assertNotNull(connectorPlugins, "connectorPlugins should not be null.");
    Optional<ConnectorPlugin> fileStreamSinkConnectorPlugin = connectorPlugins.stream()
        .filter(connectorPlugin -> "org.apache.kafka.connect.file.FileStreamSinkConnector".equalsIgnoreCase(connectorPlugin.className()))
        .findAny();
    assertTrue(fileStreamSinkConnectorPlugin.isPresent(), "FileStreamSinkConnector should be present.");
  }

  @Order(2)
  @Test
  public void validate() throws IOException {
    ValidateResponse validateResponse = client.validate(
        "FileStreamSinkConnector",
        ImmutableMap.of(
            "connector.class", "org.apache.kafka.connect.file.FileStreamSinkConnector",
            "tasks.max", "1",
            "topics", "test-topic",
            "name", "foo"
        )
    );
    assertNotNull(validateResponse);
    assertEquals("org.apache.kafka.connect.file.FileStreamSinkConnector", validateResponse.name(), "connector name does not match.");

  }

  static final String CONNECTOR_NAME = "test-connector";
  static final int MAX_TASKS = 3;
  static final Map<String, String> CONNECTOR_CONFIG = ImmutableMap.of(
      "connector.class", "org.apache.kafka.connect.file.FileStreamSinkConnector",
      "tasks.max", Integer.toString(MAX_TASKS),
      "topics", "test-topic",
      "name", CONNECTOR_NAME
  );

  @Order(3)
  @Test
  public void create() throws IOException {
    ConnectorInfo response = client.createOrUpdate("test-connector", CONNECTOR_CONFIG);
    assertNotNull(response, "response should not be null.");
    assertEquals(CONNECTOR_NAME, response.name(), "name does not match.");
    assertEquals(CONNECTOR_CONFIG, response.config(), "config does not match.");
    assertNotNull(response.tasks(), "tasks should not be null");
  }

  @Order(4)
  @Test
  public void connectorStatus() throws IOException, InterruptedException {
    ConnectorStatus statusResponse = status();
    for (int i = 0; i < MAX_TASKS; i++) {
      TaskStatus task = statusResponse.tasks().get(i);
      assertEquals(i, task.id());
      assertEquals("kafka-connect:8083", task.workerID());
    }
  }

  @Test
  @Order(5)
  public void taskStatus() throws IOException {
    TaskStatus taskStatusResponse = this.client.status(CONNECTOR_NAME, 0);
    assertNotNull(taskStatusResponse);
    assertEquals(0, taskStatusResponse.id(), "task id does not match.");
  }

  @Test
  @Order(6)
  public void config() throws IOException {
    Map<String, String> actual = this.client.config(CONNECTOR_NAME);
    assertNotNull(actual, "actual should not be null.");
    assertEquals(CONNECTOR_CONFIG, actual, "config should match.");
  }

  @Test
  @Order(7)
  public void info() throws IOException {
    ConnectorInfo actual = this.client.info(CONNECTOR_NAME);
    assertNotNull(actual, "actual should not be null.");
    assertEquals(CONNECTOR_CONFIG, actual.config(), "config should match.");
    assertEquals(CONNECTOR_NAME, actual.name());
    assertEquals(MAX_TASKS, actual.tasks().size(), "tasks.size() does not match.");
    assertEquals(ConnectorType.Sink, actual.type(), "type does not match.");
  }

  ConnectorStatus waitForState(State state) {
    final AtomicReference<ConnectorStatus> result = new AtomicReference<>(null);
    assertTimeoutPreemptively(Duration.ofSeconds(60), () -> {
      while (true) {
        ConnectorStatus status = status();
        result.set(status);
        long count = status.tasks().stream()
            .map(WorkerStatus::state)
            .filter(s -> s == state)
            .count();
        if (count == status.tasks().size()) {
          break;
        }
      }
    });
    return result.get();
  }

  ConnectorStatus status() {
    final AtomicReference<ConnectorStatus> result = new AtomicReference<>(null);
    assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
      int attempts = 0;
      while (true) {
        try {
          log.info("status() - Retrieving status for {}. Attempt {}", CONNECTOR_NAME, attempts);
          ConnectorStatus statusResponse = client.status(CONNECTOR_NAME);
          assertNotNull(statusResponse, "statusResponse should not be null.");
          assertEquals(CONNECTOR_NAME, statusResponse.name(), "Connector name does not match.");

          if (statusResponse.tasks().isEmpty()) {
            throw new KafkaConnectException(9999, "Tasks should not be empty.");
          }
          assertEquals(MAX_TASKS, statusResponse.tasks().size(), "Task count does not match.");
          result.set(statusResponse);
          return;
        } catch (KafkaConnectException ex) {
          log.debug("Exception thrown", ex);
          if (404 == ex.errorCode() && attempts > 10) {
            throw ex;
          } else {
            log.info("status() - Sleeping");
            Thread.sleep(1000);
          }
        } finally {
          attempts++;
        }
      }

    }, "Exception thrown waiting for state");
    return result.get();
  }

  @Test
  @Order(8)
  public void pause() throws IOException {
    // Make sure the connector is running
    waitForState(State.Running);
    this.client.pause(CONNECTOR_NAME);
    waitForState(State.Paused);
  }

  @Test
  @Order(9)
  public void resume() throws IOException {
    // Make sure the connector is running
    waitForState(State.Paused);
    this.client.resume(CONNECTOR_NAME);
    waitForState(State.Running);
  }

  TaskStatus waitForTaskState(int taskID, State state) {
    log.info("waitForTaskState() - taskId = {} state = {}", taskID, state);
    AtomicReference<TaskStatus> result = new AtomicReference<>(null);

    assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
      while (true) {
        TaskStatus taskStatusResponse = this.client.status(CONNECTOR_NAME, taskID);
        result.set(taskStatusResponse);
        if (taskStatusResponse.state() == state) {
          break;
        }

        Thread.sleep(1000);
      }
    });


    return result.get();
  }


  @Test
  @Order(10)
  public void restartTasks() throws IOException {
    ConnectorStatus statusResponse = waitForState(State.Running);

    for (TaskStatus taskStatusResponse : statusResponse.tasks()) {
      this.client.restart(CONNECTOR_NAME, taskStatusResponse.id());
      waitForTaskState(taskStatusResponse.id(), State.Running);
    }
    waitForState(State.Running);
  }

  @Test
  @Order(11)
  public void restart() throws IOException {
    this.client.restart(CONNECTOR_NAME);
    waitForState(State.Running);
  }

  @Test
  @Order(12)
  public void delete() throws IOException {
    this.client.delete(CONNECTOR_NAME);
  }

  @Order(13)
  @Test
  public void finishShouldBeNoConnectors() throws IOException {
    ensureNoConnectors();
  }

  @Test
  public void serverInfo() throws IOException {
    ServerInfo serverInfo = this.client.serverInfo();
    assertNotNull(serverInfo.commit());
    assertNotNull(serverInfo.kafkaClusterId());
    assertNotNull(serverInfo.version());
  }
}
