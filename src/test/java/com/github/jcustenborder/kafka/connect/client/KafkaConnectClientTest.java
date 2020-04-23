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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.jcustenborder.kafka.connect.client.model.ConnectorInfo;
import com.github.jcustenborder.kafka.connect.client.model.ConnectorStatus;
import com.github.jcustenborder.kafka.connect.client.model.CreateConnectorRequest;
import com.github.jcustenborder.kafka.connect.client.model.CreateConnectorResponse;
import com.github.jcustenborder.kafka.connect.client.model.ServerInfo;
import com.github.jcustenborder.kafka.connect.client.model.TaskConfig;
import com.github.jcustenborder.kafka.connect.client.model.TaskStatus;
import com.github.jcustenborder.kafka.connect.client.model.ValidateResponse;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.immutables.value.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;


@ExtendWith({MockRequestExtension.class})
public class KafkaConnectClientTest {
  private static final Logger log = LoggerFactory.getLogger(KafkaConnectClientTest.class);

  protected MockWebServer mockWebServer;
  protected KafkaConnectClient kafkaConnectClient;
  protected ObjectMapper objectMapper;

  @BeforeEach
  public void before() throws URISyntaxException, IOException {
    this.mockWebServer = new MockWebServer();
    this.mockWebServer.start();
    this.objectMapper = new ObjectMapper();
    this.objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    this.kafkaConnectClient = KafkaConnectClient.builder()
        .host(this.mockWebServer.getHostName())
        .port(this.mockWebServer.getPort())
        .createClient();
  }

  @AfterEach
  public void after() throws Exception {
    this.kafkaConnectClient.close();
  }

  private <A extends TestRequest, B extends TestResponse> void configure(TestCase<A, B> testCase) throws JsonProcessingException {
    for (B response : testCase.responses()) {
      MockResponse mockResponse = new MockResponse();
      mockResponse.setResponseCode(response.metadata().statusCode());
      if (null != response.metadata().headers() && !response.metadata().headers().isEmpty()) {
        response.metadata().headers().forEach(mockResponse::setHeader);
      }
      if (response.metadata().isSuccessful()) {
        if (null != response.body()) {
          String body = this.objectMapper.writeValueAsString(response.body());
          mockResponse.setBody(body);
        }
      } else {
        if (null != response.error()) {
          String body = this.objectMapper.writeValueAsString(response.error());
          mockResponse.setBody(body);
        }
      }
      this.mockWebServer.enqueue(mockResponse);
    }
  }

  private <A extends TestRequest, B extends TestResponse> void assertRequests(TestCase<A, B> testCase) throws InterruptedException, JsonProcessingException {
    for (A request : testCase.requests()) {
      RecordedRequest recordedRequest = this.mockWebServer.takeRequest(5, TimeUnit.SECONDS);
      log.info("assertRequests() - method='{}' url='{}'", recordedRequest.getMethod(), recordedRequest.getRequestUrl());
      assertEquals(request.metadata().path(), recordedRequest.getPath());
      assertEquals(request.metadata().method(), recordedRequest.getMethod());
      if (null != request.body()) {
        String body = recordedRequest.getBody().readUtf8();
        log.info("assertRequests() - method='{}' url='{}'\n{}", recordedRequest.getMethod(), recordedRequest.getRequestUrl(), body);
        Object value = objectMapper.readValue(body, request.body().getClass());
        assertEquals(request.body(), value);
      }
    }
  }

  @Value.Immutable
  @Value.Style(jdkOnly = true)
  @JsonDeserialize(as = ImmutableServerInfoTestCase.class)
  public interface ServerInfoTestCase extends TestCase<ServerInfoTestCase.ServerInfoTestRequest, ServerInfoTestCase.ServerInfoTestResponse> {


    @Value.Immutable
    @Value.Style(jdkOnly = true)
    @JsonDeserialize(as = ImmutableServerInfoTestRequest.class)
    interface ServerInfoTestRequest extends TestRequest<Object> {

    }

    @Value.Immutable
    @Value.Style(jdkOnly = true)
    @JsonDeserialize(as = ImmutableServerInfoTestResponse.class)
    interface ServerInfoTestResponse extends TestResponse<ServerInfo> {

    }
  }

  @Test
  public void serverInfo(@LoadMockResponse(path = "serverinfo.json") ServerInfoTestCase testCase) throws IOException, InterruptedException {
    configure(testCase);

    ServerInfo serverInfo = this.kafkaConnectClient.serverInfo();
    assertEquals(
        ServerInfo.builder()
            .commit("e5741b90cde98052")
            .kafkaClusterId("I4ZmrWqfT2e-upky_4fdPA")
            .version("5.5.0")
            .build(),
        serverInfo
    );
    assertRequests(testCase);
  }

  @Value.Immutable
  @Value.Style(jdkOnly = true)
  @JsonDeserialize(as = ImmutableConnectorsTestCase.class)
  public interface ConnectorsTestCase extends TestCase<ConnectorsTestCase.ConnectorsTestRequest, ConnectorsTestCase.ConnectorsTestResponse> {
    @Value.Immutable
    @Value.Style(jdkOnly = true)
    @JsonDeserialize(as = ImmutableConnectorsTestRequest.class)
    interface ConnectorsTestRequest extends TestRequest<Object> {

    }

    @Value.Immutable
    @Value.Style(jdkOnly = true)
    @JsonDeserialize(as = ImmutableConnectorsTestResponse.class)
    interface ConnectorsTestResponse extends TestResponse<List<String>> {

    }

  }

  @Test
  public void connectors(@LoadMockResponse(path = "connectors.json") ConnectorsTestCase testCase) throws IOException, InterruptedException {
    configure(testCase);
    List<String> connectors = this.kafkaConnectClient.connectors();
    assertEquals(ImmutableList.of("my-jdbc-source", "my-hdfs-sink"), connectors);
    assertRequests(testCase);
  }

  @Value.Immutable
  @Value.Style(jdkOnly = true)
  @JsonDeserialize(as = ImmutableCreateConnectorTestCase.class)
  public interface CreateConnectorTestCase extends TestCase<CreateConnectorTestCase.CreateConnectorTestRequest, CreateConnectorTestCase.CreateConnectorTestResponse> {
    @Value.Immutable
    @Value.Style(jdkOnly = true)
    @JsonDeserialize(as = ImmutableCreateConnectorTestRequest.class)
    interface CreateConnectorTestRequest extends TestRequest<CreateConnectorRequest> {

    }

    @Value.Immutable
    @Value.Style(jdkOnly = true)
    @JsonDeserialize(as = ImmutableCreateConnectorTestResponse.class)
    interface CreateConnectorTestResponse extends TestResponse<CreateConnectorResponse> {

    }
  }


  @Test
  public void createConnector(@LoadMockResponse(path = "createConnector.json") CreateConnectorTestCase testCase) throws IOException, InterruptedException {
    configure(testCase);
    CreateConnectorRequest request = CreateConnectorRequest.builder()
        .name("hdfs-sink-connector")
        .putConfig("connector.class", "io.confluent.connect.hdfs.HdfsSinkConnector")
        .putConfig("tasks.max", "10")
        .putConfig("topics", "test-topic")
        .putConfig("hdfs.url", "hdfs://fakehost:9000")
        .putConfig("hadoop.conf.dir", "/opt/hadoop/conf")
        .putConfig("hadoop.home", "/opt/hadoop")
        .putConfig("flush.size", "100")
        .putConfig("rotate.interval.ms", "1000")
        .build();
    CreateConnectorResponse actual = this.kafkaConnectClient.createConnector(request);
    CreateConnectorResponse expected = testCase.responses().get(1).body();
    assertEquals(expected, actual);
    assertRequests(testCase);
  }


  @Value.Immutable
  @Value.Style(jdkOnly = true)
  @JsonDeserialize(as = ImmutableGetConnectorTestCase.class)
  public interface GetConnectorTestCase extends TestCase<GetConnectorTestCase.GetConnectorTestRequest, GetConnectorTestCase.GetConnectorTestResponse> {
    @Value.Immutable
    @Value.Style(jdkOnly = true)
    @JsonDeserialize(as = ImmutableGetConnectorTestRequest.class)
    interface GetConnectorTestRequest extends TestRequest<Object> {

    }

    @Value.Immutable
    @Value.Style(jdkOnly = true)
    @JsonDeserialize(as = ImmutableGetConnectorTestResponse.class)
    interface GetConnectorTestResponse extends TestResponse<ConnectorInfo> {

    }
  }

  @Test
  public void info(@LoadMockResponse(path = "connector-info.json") GetConnectorTestCase testCase) throws IOException, InterruptedException {
    configure(testCase);
    ConnectorInfo actual = this.kafkaConnectClient.info("hdfs-sink-connector");
    ConnectorInfo expected = testCase.responses().get(0).body();
    assertEquals(expected, actual);
    assertRequests(testCase);
  }

  @Value.Immutable
  @Value.Style(jdkOnly = true)
  @JsonDeserialize(as = ImmutableConfigTestCase.class)
  public interface ConfigTestCase extends TestCase<ConfigTestCase.ConfigTestRequest, ConfigTestCase.ConfigTestResponse> {
    @Value.Immutable
    @Value.Style(jdkOnly = true)
    @JsonDeserialize(as = ImmutableConfigTestRequest.class)
    interface ConfigTestRequest extends TestRequest<Object> {

    }

    @Value.Immutable
    @Value.Style(jdkOnly = true)
    @JsonDeserialize(as = ImmutableConfigTestResponse.class)
    interface ConfigTestResponse extends TestResponse<Map<String, String>> {

    }
  }

  @Test
  public void config(@LoadMockResponse(path = "config.json") ConfigTestCase testCase) throws IOException, InterruptedException {
    configure(testCase);
    Map<String, String> actual = this.kafkaConnectClient.config("hdfs-sink-connector");
    Map<String, String> expected = testCase.responses().get(0).body();
    assertEquals(expected, actual);
    assertRequests(testCase);
  }

  @Test
  public void configNotFound(@LoadMockResponse(path = "config-not-found.json") ConfigTestCase testCase) throws IOException, InterruptedException {
    configure(testCase);
    KafkaConnectException exception = assertThrows(KafkaConnectException.class, () -> {
      Map<String, String> actual = this.kafkaConnectClient.config("hdfs-sink-connector");
    });
    assertEquals(404, (int) exception.errorCode());
    assertRequests(testCase);
  }

  @Value.Immutable
  @Value.Style(jdkOnly = true)
  @JsonDeserialize(as = ImmutableCreateOrUpdateTestCase.class)
  public interface CreateOrUpdateTestCase extends TestCase<CreateOrUpdateTestCase.CreateOrUpdateTestRequest, CreateOrUpdateTestCase.CreateOrUpdateTestResponse> {
    @Value.Immutable
    @Value.Style(jdkOnly = true)
    @JsonDeserialize(as = ImmutableCreateOrUpdateTestRequest.class)
    interface CreateOrUpdateTestRequest extends TestRequest<Map<String, String>> {

    }

    @Value.Immutable
    @Value.Style(jdkOnly = true)
    @JsonDeserialize(as = ImmutableCreateOrUpdateTestResponse.class)
    interface CreateOrUpdateTestResponse extends TestResponse<ConnectorInfo> {

    }
  }

  @Test
  public void createOrUpdate(@LoadMockResponse(path = "createOrUpdate.json") CreateOrUpdateTestCase testCase) throws IOException, InterruptedException {
    Map<String, String> config = new LinkedHashMap<>();
    config.put("connector.class", "io.confluent.connect.hdfs.HdfsSinkConnector");
    config.put("tasks.max", "10");
    config.put("topics", "test-topic");
    config.put("hdfs.url", "hdfs://fakehost:9000");
    config.put("hadoop.conf.dir", "/opt/hadoop/conf");
    config.put("hadoop.home", "/opt/hadoop");
    config.put("flush.size", "100");
    config.put("rotate.interval.ms", "1000");

    configure(testCase);
    ConnectorInfo actual = this.kafkaConnectClient.createOrUpdate("hdfs-sink-connector", config);
    ConnectorInfo expected = testCase.responses().get(0).body();
    assertEquals(expected, actual);
    assertRequests(testCase);
  }

  @Value.Immutable
  @Value.Style(jdkOnly = true)
  @JsonDeserialize(as = ImmutableConnectorStatusTestCase.class)
  public interface ConnectorStatusTestCase extends TestCase<ConnectorStatusTestCase.ConnectorStatusTestRequest, ConnectorStatusTestCase.ConnectorStatusTestResponse> {
    @Value.Immutable
    @Value.Style(jdkOnly = true)
    @JsonDeserialize(as = ImmutableConnectorStatusTestRequest.class)
    interface ConnectorStatusTestRequest extends TestRequest<Object> {

    }

    @Value.Immutable
    @Value.Style(jdkOnly = true)
    @JsonDeserialize(as = ImmutableConnectorStatusTestResponse.class)
    interface ConnectorStatusTestResponse extends TestResponse<ConnectorStatus> {

    }
  }

  @Test
  public void statusConnector(@LoadMockResponse(path = "statusConnector.json") ConnectorStatusTestCase testCase) throws IOException, InterruptedException {
    configure(testCase);
    ConnectorStatus actual = this.kafkaConnectClient.status("hdfs-sink-connector");
    ConnectorStatus expected = testCase.responses().get(0).body();
    assertEquals(expected, actual);
    assertRequests(testCase);
  }

  @Value.Immutable
  @Value.Style(jdkOnly = true)
  @JsonDeserialize(as = ImmutableRestartConnectorTestCase.class)
  public interface RestartConnectorTestCase extends TestCase<RestartConnectorTestCase.RestartConnectorTestRequest, RestartConnectorTestCase.RestartConnectorTestResponse> {
    @Value.Immutable
    @Value.Style(jdkOnly = true)
    @JsonDeserialize(as = ImmutableRestartConnectorTestRequest.class)
    interface RestartConnectorTestRequest extends TestRequest<Object> {

    }

    @Value.Immutable
    @Value.Style(jdkOnly = true)
    @JsonDeserialize(as = ImmutableRestartConnectorTestResponse.class)
    interface RestartConnectorTestResponse extends TestResponse<Object> {

    }
  }

  @Test
  public void restartConnector(@LoadMockResponse(path = "restartConnector.json") RestartConnectorTestCase testCase) throws IOException, InterruptedException {
    configure(testCase);
    this.kafkaConnectClient.restart("hdfs-sink-connector");
    assertRequests(testCase);
  }

  @Value.Immutable
  @Value.Style(jdkOnly = true)
  @JsonDeserialize(as = ImmutablePauseConnectorTestCase.class)
  public interface PauseConnectorTestCase extends TestCase<PauseConnectorTestCase.PauseConnectorTestRequest, PauseConnectorTestCase.PauseConnectorTestResponse> {
    @Value.Immutable
    @Value.Style(jdkOnly = true)
    @JsonDeserialize(as = ImmutablePauseConnectorTestRequest.class)
    interface PauseConnectorTestRequest extends TestRequest<Object> {

    }

    @Value.Immutable
    @Value.Style(jdkOnly = true)
    @JsonDeserialize(as = ImmutablePauseConnectorTestResponse.class)
    interface PauseConnectorTestResponse extends TestResponse<Object> {

    }
  }

  @Test
  public void pauseConnector(@LoadMockResponse(path = "pauseConnector.json") PauseConnectorTestCase testCase) throws IOException, InterruptedException {
    configure(testCase);
    this.kafkaConnectClient.pause("hdfs-sink-connector");
    assertRequests(testCase);
  }

  @Value.Immutable
  @Value.Style(jdkOnly = true)
  @JsonDeserialize(as = ImmutableResumeConnectorTestCase.class)
  public interface ResumeConnectorTestCase extends TestCase<ResumeConnectorTestCase.ResumeConnectorTestRequest, ResumeConnectorTestCase.ResumeConnectorTestResponse> {
    @Value.Immutable
    @Value.Style(jdkOnly = true)
    @JsonDeserialize(as = ImmutableResumeConnectorTestRequest.class)
    interface ResumeConnectorTestRequest extends TestRequest<Object> {

    }

    @Value.Immutable
    @Value.Style(jdkOnly = true)
    @JsonDeserialize(as = ImmutableResumeConnectorTestResponse.class)
    interface ResumeConnectorTestResponse extends TestResponse<Object> {

    }
  }

  @Test
  public void resumeConnector(@LoadMockResponse(path = "resumeConnector.json") ResumeConnectorTestCase testCase) throws IOException, InterruptedException {
    configure(testCase);
    this.kafkaConnectClient.resume("hdfs-sink-connector");
    assertRequests(testCase);
  }


  @Value.Immutable
  @Value.Style(jdkOnly = true)
  @JsonDeserialize(as = ImmutableDeleteConnectorTestCase.class)
  public interface DeleteConnectorTestCase extends TestCase<DeleteConnectorTestCase.DeleteConnectorTestRequest, DeleteConnectorTestCase.DeleteConnectorTestResponse> {
    @Value.Immutable
    @Value.Style(jdkOnly = true)
    @JsonDeserialize(as = ImmutableDeleteConnectorTestRequest.class)
    interface DeleteConnectorTestRequest extends TestRequest<Object> {

    }

    @Value.Immutable
    @Value.Style(jdkOnly = true)
    @JsonDeserialize(as = ImmutableDeleteConnectorTestResponse.class)
    interface DeleteConnectorTestResponse extends TestResponse<Object> {

    }
  }

  @Test
  public void deleteConnector(@LoadMockResponse(path = "deleteConnector.json") DeleteConnectorTestCase testCase) throws IOException, InterruptedException {
    configure(testCase);
    this.kafkaConnectClient.delete("hdfs-sink-connector");
    assertRequests(testCase);
  }


  @Value.Immutable
  @Value.Style(jdkOnly = true)
  @JsonDeserialize(as = ImmutableTaskConfigsTestCase.class)
  public interface TaskConfigsTestCase extends TestCase<TaskConfigsTestCase.TaskConfigsTestRequest, TaskConfigsTestCase.TaskConfigsTestResponse> {
    @Value.Immutable
    @Value.Style(jdkOnly = true)
    @JsonDeserialize(as = ImmutableTaskConfigsTestRequest.class)
    interface TaskConfigsTestRequest extends TestRequest<Object> {

    }

    @Value.Immutable
    @Value.Style(jdkOnly = true)
    @JsonDeserialize(as = ImmutableTaskConfigsTestResponse.class)
    interface TaskConfigsTestResponse extends TestResponse<List<TaskConfig>> {

    }
  }

  @Test
  public void taskConfigs(@LoadMockResponse(path = "taskConfigs.json") TaskConfigsTestCase testCase) throws IOException, InterruptedException {
    configure(testCase);
    List<TaskConfig> actual = this.kafkaConnectClient.taskConfigs("hdfs-sink-connector");
    List<TaskConfig> expected = testCase.responses().get(0).body();
    assertEquals(expected, actual);
    assertRequests(testCase);
  }


  @Value.Immutable
  @Value.Style(jdkOnly = true)
  @JsonDeserialize(as = ImmutableTaskStatusTestCase.class)
  public interface TaskStatusTestCase extends TestCase<TaskStatusTestCase.TaskStatusTestRequest, TaskStatusTestCase.TaskStatusTestResponse> {
    @Value.Immutable
    @Value.Style(jdkOnly = true)
    @JsonDeserialize(as = ImmutableTaskStatusTestRequest.class)
    interface TaskStatusTestRequest extends TestRequest<Object> {

    }

    @Value.Immutable
    @Value.Style(jdkOnly = true)
    @JsonDeserialize(as = ImmutableTaskStatusTestResponse.class)
    interface TaskStatusTestResponse extends TestResponse<TaskStatus> {

    }
  }

  @Test
  public void taskStatus(@LoadMockResponse(path = "taskStatus.json") TaskStatusTestCase testCase) throws IOException, InterruptedException {
    configure(testCase);
    TaskStatus actual = this.kafkaConnectClient.status("hdfs-sink-connector", 1);
    TaskStatus expected = testCase.responses().get(0).body();
    assertEquals(expected, actual);
    assertRequests(testCase);
  }


  @Value.Immutable
  @Value.Style(jdkOnly = true)
  @JsonDeserialize(as = ImmutableTaskRestartTestCase.class)
  public interface TaskRestartTestCase extends TestCase<TaskRestartTestCase.TaskRestartTestRequest, TaskRestartTestCase.TaskRestartTestResponse> {
    @Value.Immutable
    @Value.Style(jdkOnly = true)
    @JsonDeserialize(as = ImmutableTaskRestartTestRequest.class)
    interface TaskRestartTestRequest extends TestRequest<Object> {

    }

    @Value.Immutable
    @Value.Style(jdkOnly = true)
    @JsonDeserialize(as = ImmutableTaskRestartTestResponse.class)
    interface TaskRestartTestResponse extends TestResponse<Object> {

    }
  }

  @Test
  public void taskRestart(@LoadMockResponse(path = "taskRestart.json") TaskRestartTestCase testCase) throws IOException, InterruptedException {
    configure(testCase);
    this.kafkaConnectClient.restart("hdfs-sink-connector", 1);
    assertRequests(testCase);
  }

  @Value.Immutable
  @Value.Style(jdkOnly = true)
  @JsonDeserialize(as = ImmutableValidateTestCase.class)
  public interface ValidateTestCase extends TestCase<ValidateTestCase.ValidateTestRequest, ValidateTestCase.ValidateTestResponse> {
    @Value.Immutable
    @Value.Style(jdkOnly = true)
    @JsonDeserialize(as = ImmutableValidateTestRequest.class)
    interface ValidateTestRequest extends TestRequest<Map<String, String>> {

    }

    @Value.Immutable
    @Value.Style(jdkOnly = true)
    @JsonDeserialize(as = ImmutableValidateTestResponse.class)
    interface ValidateTestResponse extends TestResponse<ValidateResponse> {

    }
  }

  @Test
  public void validate(@LoadMockResponse(path = "validate.json") ValidateTestCase testCase) throws IOException, InterruptedException {
    Map<String, String> config = ImmutableMap.of(
        "connector.class", "io.confluent.connect.activemq.ActiveMQSourceConnector",
        "tasks.max", "1"
    );
    configure(testCase);
    ValidateResponse actual = this.kafkaConnectClient.validate("io.confluent.connect.activemq.ActiveMQSourceConnector", config);
    ValidateResponse expected = testCase.responses().get(0).body();
    assertEquals(expected, actual);
    assertRequests(testCase);
  }


  public static File getOutputFile() {
    String fileName = getMethodName(3) + ".json";
    return new File("src/test/resources/com/github/jcustenborder/kafka/connect/client", fileName);
  }

  public static String getMethodName(final int depth) {
    List<String> methodNames = Stream.of(Thread.currentThread().getStackTrace())
        .map(StackTraceElement::getMethodName)
        .collect(Collectors.toList());
    return methodNames.get(depth);
  }


}
