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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jcustenborder.kafka.connect.client.model.ConnectorInfo;
import com.github.jcustenborder.kafka.connect.client.model.ConnectorPlugin;
import com.github.jcustenborder.kafka.connect.client.model.ConnectorStatus;
import com.github.jcustenborder.kafka.connect.client.model.CreateConnectorRequest;
import com.github.jcustenborder.kafka.connect.client.model.CreateConnectorResponse;
import com.github.jcustenborder.kafka.connect.client.model.ServerInfo;
import com.github.jcustenborder.kafka.connect.client.model.TaskConfig;
import com.github.jcustenborder.kafka.connect.client.model.TaskStatus;
import com.github.jcustenborder.kafka.connect.client.model.ValidateResponse;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

class KafkaConnectClientImpl implements AsyncKafkaConnectClient, KafkaConnectClient {
  static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
  static final RequestBody EMPTY = RequestBody.create(null, new byte[]{});
  static final TypeReference<Map<String, String>> CONFIG_TYPE = new TypeReference<Map<String, String>>() {
  };
  static final TypeReference<List<String>> CONNECTORS_TYPE = new TypeReference<List<String>>() {
  };
  static final TypeReference<List<TaskConfig>> TASKCONFIG_TYPE = new TypeReference<List<TaskConfig>>() {
  };
  static final TypeReference<List<ConnectorPlugin>> CONNECTOR_PLUGIN_TYPE = new TypeReference<List<ConnectorPlugin>>() {
  };
  private static final Logger log = LoggerFactory.getLogger(KafkaConnectClientImpl.class);
  private final HttpUrl baseUrl;
  private final OkHttpClient client;
  private final ObjectMapper objectMapper;
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


  KafkaConnectClientImpl(
      HttpUrl baseUrl,
      OkHttpClient client,
      String username,
      String password,
      ObjectMapper objectMapper) {
    this.baseUrl = baseUrl;
    this.client = client;
    this.objectMapper = objectMapper;
  }

  static HttpUrl addPathSegments(HttpUrl baseUrl, Iterable<String> parts) {
    HttpUrl.Builder builder = baseUrl.newBuilder();
    for (String part : parts) {
      builder = builder.addPathSegment(part);
    }
    return builder.build();
  }

  static <T> T get(Future<T> future) throws IOException {
    try {
      return future.get();
    } catch (InterruptedException e) {
      throw new IOException(e);
    } catch (ExecutionException ex) {
      if (ex.getCause() instanceof KafkaConnectException) {
        throw (KafkaConnectException) ex.getCause();
      } else {
        throw new IOException(ex);
      }
    }
  }

  HttpUrl baseUrl(String... parts) {
    return addPathSegments(this.baseUrl, Arrays.asList(parts));
  }

  HttpUrl connectorsUrl(String... parts) {
    List<String> segments = new ArrayList<>();
    segments.add("connectors");
    segments.addAll(Arrays.asList(parts));
    return addPathSegments(this.baseUrl, segments);
  }

  HttpUrl connectorsPluginsUrl(String... parts) {
    List<String> segments = new ArrayList<>();
    segments.add("connector-plugins");
    segments.addAll(Arrays.asList(parts));
    return addPathSegments(this.baseUrl, segments);
  }

  void checkConnectorConfig(Map<String, String> config) {
    //TODO: Comeback and do some validation.
//    Preconditions.checkNotNull("config", "config cannot be null");
//    Preconditions.checkState(config.containsKey("connector.class"), "connector.class must be specified.");
//    Preconditions.checkState(config.containsKey("tasks.max"), "tasks.max must be specified.");
  }

  @Override
  public List<String> connectors() throws IOException {
    CompletableFuture<List<String>> future = connectorsAsync();
    return get(future);
  }

  @Override
  public CreateConnectorResponse createConnector(CreateConnectorRequest request) throws IOException {
    return get(createConnectorAsync(request));
  }


  @Override
  public ConnectorInfo createOrUpdate(String name, Map<String, String> config) throws IOException {
    CompletableFuture<ConnectorInfo> future = createOrUpdateAsync(name, config);
    return get(future);
  }

  @Override
  public ConnectorInfo info(String name) throws IOException {
    CompletableFuture<ConnectorInfo> future = infoAsync(name);
    return get(future);
  }

  @Override
  public Map<String, String> config(String name) throws IOException {
    CompletableFuture<Map<String, String>> future = configAsync(name);
    return get(future);

  }

  @Override
  public void delete(String name) throws IOException {
    CompletableFuture<Void> future = deleteAsync(name);
    get(future);
  }

  @Override
  public void restart(String name) throws IOException {
    CompletableFuture<Void> future = restartAsync(name);
    get(future);
  }

  @Override
  public void pause(String name) throws IOException {
    CompletableFuture<Void> future = pauseAsync(name);
    get(future);
  }

  @Override
  public void resume(String name) throws IOException {
    CompletableFuture<Void> future = resumeAsync(name);
    get(future);
  }

  @Override
  public ConnectorStatus status(String name) throws IOException {
    CompletableFuture<ConnectorStatus> future = statusAsync(name);
    return get(future);
  }

  @Override
  public TaskStatus status(String name, int taskId) throws IOException {
    CompletableFuture<TaskStatus> future = statusAsync(name, taskId);
    return get(future);
  }

  @Override
  public void restart(String name, int taskId) throws IOException {
    CompletableFuture<Void> future = restartAsync(name, taskId);
    get(future);
  }


  @Override
  public List<ConnectorPlugin> connectorPlugins() throws IOException {
    CompletableFuture<List<ConnectorPlugin>> future = connectorPluginsAsync();
    return get(future);
  }

  @Override
  public ValidateResponse validate(String name, Map<String, String> config) throws IOException {
    CompletableFuture<ValidateResponse> future = validateAsync(name, config);
    return get(future);
  }

  @Override
  public ServerInfo serverInfo() throws IOException {
    CompletableFuture<ServerInfo> future = serverInfoAsync();
    return get(future);
  }

  private <T> CompletableFuture<T> executeRequest(Request request, TypeReference<T> type) {
    final CompletableFuture<T> futureResult = new CompletableFuture<>();
    final TypeReferenceCallback<T> callback = new TypeReferenceCallback<>(
        this.client,
        this.objectMapper,
        futureResult,
        10,
        scheduler,
        3000,
        type
    );
    callback.newCall(request);
    return futureResult;
  }

  private <T> CompletableFuture<T> executeRequest(Request request, Class<T> type) {
    final CompletableFuture<T> futureResult = new CompletableFuture<>();
    final ClassCallback<T> callback = new ClassCallback<>(
        this.client,
        this.objectMapper,
        futureResult,
        10,
        scheduler,
        3000,
        type
    );
    callback.newCall(request);
    return futureResult;
  }

  Request.Builder newBuilder() {
    return new Request.Builder();
  }

  @Override
  public CompletableFuture<List<String>> connectorsAsync() {
    HttpUrl url = connectorsUrl();
    log.trace("connectorsAsync() - url = '{}'", url);

    Request request = newBuilder()
        .url(url)
        .get()
        .build();
    CompletableFuture<List<String>> result = executeRequest(request, CONNECTORS_TYPE);
    return result;
  }

  @Override
  public CompletableFuture<CreateConnectorResponse> createConnectorAsync(CreateConnectorRequest request) {
    HttpUrl url = connectorsUrl();
    log.trace("createConnector() url = '{}' request = '{}'", url, request);
    Request httpRequest = newBuilder()
        .url(url)
        .post(body(request))
        .build();
    return executeRequest(httpRequest, CreateConnectorResponse.class);
  }

  protected RequestBody body(Object o) {
    try {
      byte[] body = this.objectMapper.writeValueAsBytes(o);
      RequestBody requestBody = RequestBody.create(body, JSON);
      return requestBody;
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public CompletableFuture<ConnectorInfo> createOrUpdateAsync(String name, Map<String, String> config) {
//    Preconditions.checkNotNull(name, "name cannot be null");
    checkConnectorConfig(config);
    HttpUrl connectorUrl = connectorsUrl(name, "config");
    log.trace("createOrUpdateAsync() - url = '{}'", connectorUrl);
    Request request = newBuilder()
        .url(connectorUrl)
        .put(body(config))
        .build();
    return executeRequest(request, ConnectorInfo.class);
  }

  @Override
  public CompletableFuture<ConnectorInfo> infoAsync(String name) {
//    Preconditions.checkNotNull(name, "name cannot be null");
    HttpUrl connectorUrl = connectorsUrl(name);
    Request request = newBuilder()
        .get()
        .url(connectorUrl)
        .build();
    return executeRequest(request, ConnectorInfo.class);
  }


  @Override
  public CompletableFuture<Map<String, String>> configAsync(String name) {
    HttpUrl url = connectorsUrl(name, "config");
    log.trace("connectors() - url = '{}'", url);
    Request request = newBuilder()
        .url(url)
        .get()
        .build();
    return executeRequest(request, CONFIG_TYPE);
  }

  @Override
  public CompletableFuture<Void> deleteAsync(String name) {
//    Preconditions.checkNotNull(name, "name cannot be null");
    HttpUrl connectorUrl = connectorsUrl(name);
    Request request = newBuilder()
        .url(connectorUrl)
        .delete()
        .build();
    return executeRequest(request, Void.class);
  }

  @Override
  public CompletableFuture<Void> restartAsync(String name) {
    HttpUrl connectorUrl = connectorsUrl(name, "restart");
    Request request = newBuilder()
        .url(connectorUrl)
        .post(EMPTY)
        .build();
    return executeRequest(request, Void.class);
  }

  @Override
  public CompletableFuture<Void> pauseAsync(String name) {
    HttpUrl connectorUrl = connectorsUrl(name, "pause");
    Request request = newBuilder()
        .url(connectorUrl)
        .put(EMPTY)
        .build();
    return executeRequest(request, Void.class);
  }

  @Override
  public CompletableFuture<Void> resumeAsync(String name) {
    HttpUrl connectorUrl = connectorsUrl(name, "resume");
    Request request = newBuilder()
        .url(connectorUrl)
        .put(EMPTY)
        .build();
    return executeRequest(request, Void.class);
  }

  @Override
  public CompletableFuture<ConnectorStatus> statusAsync(String name) {
    HttpUrl connectorUrl = connectorsUrl(name, "status");
    Request request = newBuilder()
        .url(connectorUrl)
        .build();
    return executeRequest(request, ConnectorStatus.class);
  }

  @Override
  public CompletableFuture<TaskStatus> statusAsync(String name, int taskId) {
    HttpUrl connectorUrl = connectorsUrl(name, "tasks", Integer.toString(taskId), "status");
    Request request = newBuilder()
        .url(connectorUrl)
        .build();
    return executeRequest(request, TaskStatus.class);
  }

  @Override
  public CompletableFuture<Void> restartAsync(String name, int taskId) {
    HttpUrl connectorUrl = connectorsUrl(name, "tasks", Integer.toString(taskId), "restart");
    Request request = newBuilder()
        .url(connectorUrl)
        .post(EMPTY)
        .build();
    return executeRequest(request, Void.class);
  }

  @Override
  public List<TaskConfig> taskConfigs(String name) throws IOException {
    CompletableFuture<List<TaskConfig>> future = taskConfigsAsync(name);
    return get(future);
  }

  @Override
  public CompletableFuture<List<ConnectorPlugin>> connectorPluginsAsync() {
    HttpUrl pluginsUrl = connectorsPluginsUrl();
    Request request = newBuilder()
        .url(pluginsUrl)
        .build();
    return executeRequest(request, CONNECTOR_PLUGIN_TYPE);
  }

  @Override
  public CompletableFuture<ValidateResponse> validateAsync(String name, Map<String, String> config) {
    HttpUrl url = connectorsPluginsUrl(
        name,
        "config",
        "validate"
    );
    Request request = newBuilder()
        .url(url)
        .put(body(config))
        .build();
    return executeRequest(request, ValidateResponse.class);
  }

  @Override
  public CompletableFuture<ServerInfo> serverInfoAsync() {
    Request request = newBuilder()
        .url(this.baseUrl)
        .build();
    return executeRequest(request, ServerInfo.class);
  }

  @Override
  public CompletableFuture<List<TaskConfig>> taskConfigsAsync(String name) {
    HttpUrl connectorUrl = connectorsUrl(name, "tasks");
    Request request = newBuilder()
        .get()
        .url(connectorUrl)
        .build();
    return executeRequest(request, TASKCONFIG_TYPE);
  }


  @Override
  public void close() throws Exception {
    this.scheduler.shutdown();
  }
}
