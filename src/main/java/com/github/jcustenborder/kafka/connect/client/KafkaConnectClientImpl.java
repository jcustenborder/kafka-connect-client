/**
 * Copyright © 2017 Jeremy Custenborder (jcustenborder@gmail.com)
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

import com.github.jcustenborder.kafka.connect.client.model.ConnectorPlugin;
import com.github.jcustenborder.kafka.connect.client.model.ConnectorStatusResponse;
import com.github.jcustenborder.kafka.connect.client.model.CreateOrUpdateConnectorResponse;
import com.github.jcustenborder.kafka.connect.client.model.GetConnectorResponse;
import com.github.jcustenborder.kafka.connect.client.model.ServerInfo;
import com.github.jcustenborder.kafka.connect.client.model.TaskStatusResponse;
import com.github.jcustenborder.kafka.connect.client.model.ValidateResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

class KafkaConnectClientImpl implements AsyncKafkaConnectClient, KafkaConnectClient {
  private static final Logger log = LoggerFactory.getLogger(KafkaConnectClientImpl.class);

  static {
    SLF4JLogBridge.init();
  }

  final ExecutorService executorService;
  final GenericUrl baseUrl;
  final HttpRequestFactory httpRequestFactory;
  final JsonFactory jsonFactory;

  KafkaConnectClientImpl(ExecutorService executorService, GenericUrl baseUrl, HttpRequestFactory httpRequestFactory, JsonFactory jsonFactory) {
    this.executorService = executorService;
    this.baseUrl = baseUrl;
    this.httpRequestFactory = httpRequestFactory;
    this.jsonFactory = jsonFactory;
  }

  GenericUrl connectorsUrl(String... parts) {
    GenericUrl result = this.baseUrl.clone();
    List<String> pathParts = new ArrayList<>(2 + parts.length);
    pathParts.add("");
    pathParts.add("connectors");
    pathParts.addAll(Arrays.asList(parts));
    result.setPathParts(pathParts);
    return result;
  }

  GenericUrl connectorsPluginsUrl(String... parts) {
    GenericUrl result = this.baseUrl.clone();
    List<String> pathParts = new ArrayList<>(2 + parts.length);
    pathParts.add("");
    pathParts.add("connector-plugins");
    pathParts.addAll(Arrays.asList(parts));
    result.setPathParts(pathParts);
    return result;
  }


  static final TypeToken<List<String>> CONNECTORS_TYPE = new TypeToken<List<String>>() {
  };

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

  void checkConnectorConfig(Map<String, String> config) {
    Preconditions.checkNotNull("config", "config cannot be null");
    Preconditions.checkState(config.containsKey("connector.class"), "connector.class must be specified.");
    Preconditions.checkState(config.containsKey("tasks.max"), "tasks.max must be specified.");
  }

  @Override
  public List<String> connectors() throws IOException {
    CompletableFuture<List<String>> future = connectorsAsync();
    return get(future);
  }


  @Override
  public CreateOrUpdateConnectorResponse createOrUpdate(String name, Map<String, String> config) throws IOException {
    CompletableFuture<CreateOrUpdateConnectorResponse> future = createOrUpdateAsync(name, config);
    return get(future);
  }

  @Override
  public GetConnectorResponse get(String name) throws IOException {
    CompletableFuture<GetConnectorResponse> future = getAsync(name);
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
  public ConnectorStatusResponse status(String name) throws IOException {
    CompletableFuture<ConnectorStatusResponse> future = statusAsync(name);
    return get(future);
  }

  @Override
  public TaskStatusResponse status(String name, int taskId) throws IOException {
    CompletableFuture<TaskStatusResponse> future = statusAsync(name, taskId);
    return get(future);
  }

  @Override
  public void restart(String name, int taskId) throws IOException {
    CompletableFuture<Void> future = restartAsync(name, taskId);
    get(future);
  }

  static final TypeToken<List<ConnectorPlugin>> CONNECTOR_PLUGIN_TYPE = new TypeToken<List<ConnectorPlugin>>() {
  };

  @Override
  public List<ConnectorPlugin> connectorPlugins() throws IOException {
    CompletableFuture<List<ConnectorPlugin>> future = connectorPluginsAsync();
    return get(future);
  }

  @Override
  public ValidateResponse validate(String name, ImmutableMap<Object, Object> config) throws IOException {
    CompletableFuture<ValidateResponse> future = validateAsync(name, config);
    return get(future);
  }

  @Override
  public ServerInfo serverInfo() throws IOException {
    CompletableFuture<ServerInfo> future = serverInfoAsync();
    return get(future);
  }

  private void checkAndRaiseException(HttpResponse httpResponse) throws IOException {
    log.trace(
        "checkAndRaiseException() - statusCode = '{}' successStatusCode = '{}'",
        httpResponse.getStatusCode(),
        httpResponse.isSuccessStatusCode()
    );
    if (!httpResponse.isSuccessStatusCode()) {
      KafkaConnectException ex = httpResponse.parseAs(KafkaConnectException.class);
      throw ex;
    }
  }

  private CompletableFuture<Void> parseNoResponse(HttpRequest request) throws IOException {
    CompletableFuture<Void> futureResult = new CompletableFuture<>();
    Future<HttpResponse> responseFuture = request.executeAsync(this.executorService);
    CompletableFuture.runAsync(() -> {
      try {
        log.trace("parseNoResponse() - url = '{}'", request.getUrl());
        HttpResponse httpResponse = responseFuture.get();
        checkAndRaiseException(httpResponse);
        futureResult.complete(null);
      } catch (InterruptedException e) {
        futureResult.completeExceptionally(e);
      } catch (ExecutionException e) {
        futureResult.completeExceptionally(e.getCause());
      } catch (Throwable ex) {
        futureResult.completeExceptionally(ex);
      }
    }, this.executorService);
    return futureResult;
  }

  private <T> CompletableFuture<T> parseAs(HttpRequest request, TypeToken<T> token) {
    CompletableFuture<T> futureResult = new CompletableFuture<>();
    Future<HttpResponse> responseFuture = request.executeAsync(this.executorService);
    CompletableFuture.runAsync(() -> {
      try {
        log.trace("parseAs() - url = '{}'", request.getUrl());
        HttpResponse httpResponse = responseFuture.get();
        checkAndRaiseException(httpResponse);
        Type type = token.getType();
        Object result = httpResponse.parseAs(type);
        futureResult.complete((T) result);
      } catch (InterruptedException e) {
        futureResult.completeExceptionally(e);
      } catch (ExecutionException e) {
        futureResult.completeExceptionally(e.getCause());
      } catch (Throwable ex) {
        futureResult.completeExceptionally(ex);
      }
    }, this.executorService);


    return futureResult;
  }

  private <T> CompletableFuture<T> parseAs(HttpRequest request, Class<T> cls) {
    CompletableFuture<T> futureResult = new CompletableFuture<>();
    Future<HttpResponse> responseFuture = request.executeAsync(this.executorService);
    CompletableFuture.runAsync(() -> {
      try {
        log.trace("parseAs() - url = '{}'", request.getUrl());
        HttpResponse httpResponse = responseFuture.get();
        checkAndRaiseException(httpResponse);
        Object result = httpResponse.parseAs(cls);
        futureResult.complete((T) result);
      } catch (InterruptedException e) {
        futureResult.completeExceptionally(e);
      } catch (ExecutionException e) {
        futureResult.completeExceptionally(e.getCause());
      } catch (Throwable ex) {
        futureResult.completeExceptionally(ex);
      }
    }, this.executorService);


    return futureResult;
  }

  @Override
  public CompletableFuture<List<String>> connectorsAsync() throws IOException {
    GenericUrl url = connectorsUrl();
    log.trace("connectorsAsync() - url = '{}'", url);
    HttpRequest request = this.httpRequestFactory.buildGetRequest(url);
    CompletableFuture<List<String>> result = parseAs(request, CONNECTORS_TYPE);
    return result;
  }

  @Override
  public CompletableFuture<CreateOrUpdateConnectorResponse> createOrUpdateAsync(String name, Map<String, String> config) throws IOException {
    Preconditions.checkNotNull(name, "name cannot be null");
    checkConnectorConfig(config);
    GenericUrl connectorUrl = connectorsUrl(name, "config");
    log.trace("createOrUpdateAsync() - url = '{}'", connectorUrl);
    JsonHttpContent content = new JsonHttpContent(this.jsonFactory, config);
    HttpRequest httpRequest = this.httpRequestFactory.buildPutRequest(connectorUrl, content);
    return parseAs(httpRequest, CreateOrUpdateConnectorResponse.class);
  }

  @Override
  public CompletableFuture<GetConnectorResponse> getAsync(String name) throws IOException {
    Preconditions.checkNotNull(name, "name cannot be null");
    GenericUrl connectorUrl = connectorsUrl(name);
    HttpRequest httpRequest = this.httpRequestFactory.buildGetRequest(connectorUrl);
    return parseAs(httpRequest, GetConnectorResponse.class);
  }

  static final TypeToken<Map<String, String>> CONFIG_TYPE = new TypeToken<Map<String, String>>() {
  };

  @Override
  public CompletableFuture<Map<String, String>> configAsync(String name) throws IOException {
    GenericUrl url = connectorsUrl(name, "config");
    log.trace("connectors() - url = '{}'", url);
    HttpRequest request = this.httpRequestFactory.buildGetRequest(url);
    return parseAs(request, CONFIG_TYPE);
  }

  @Override
  public CompletableFuture<Void> deleteAsync(String name) throws IOException {
    Preconditions.checkNotNull(name, "name cannot be null");
    GenericUrl connectorUrl = connectorsUrl(name);
    HttpRequest httpRequest = this.httpRequestFactory.buildDeleteRequest(connectorUrl);
    return parseNoResponse(httpRequest);
  }

  @Override
  public CompletableFuture<Void> restartAsync(String name) throws IOException {
    GenericUrl connectorUrl = connectorsUrl(name, "restart");
    HttpRequest httpRequest = this.httpRequestFactory.buildPostRequest(connectorUrl, null);
    return parseNoResponse(httpRequest);
  }

  @Override
  public CompletableFuture<Void> pauseAsync(String name) throws IOException {
    GenericUrl connectorUrl = connectorsUrl(name, "pause");
    HttpRequest httpRequest = this.httpRequestFactory.buildPutRequest(connectorUrl, null);
    return parseNoResponse(httpRequest);
  }

  @Override
  public CompletableFuture<Void> resumeAsync(String name) throws IOException {
    GenericUrl connectorUrl = connectorsUrl(name, "resume");
    HttpRequest httpRequest = this.httpRequestFactory.buildPutRequest(connectorUrl, null);
    return parseNoResponse(httpRequest);
  }

  @Override
  public CompletableFuture<ConnectorStatusResponse> statusAsync(String name) throws IOException {
    GenericUrl connectorUrl = connectorsUrl(name, "status");
    HttpRequest httpRequest = this.httpRequestFactory.buildGetRequest(connectorUrl);
    return parseAs(httpRequest, ConnectorStatusResponse.class);
  }

  @Override
  public CompletableFuture<TaskStatusResponse> statusAsync(String name, int taskId) throws IOException {
    GenericUrl connectorUrl = connectorsUrl(name, "tasks", Integer.toString(taskId), "status");
    HttpRequest httpRequest = this.httpRequestFactory.buildGetRequest(connectorUrl);
    return parseAs(httpRequest, TaskStatusResponse.class);
  }

  @Override
  public CompletableFuture<Void> restartAsync(String name, int taskId) throws IOException {
    GenericUrl connectorUrl = connectorsUrl(name, "tasks", Integer.toString(taskId), "restart");
    HttpRequest httpRequest = this.httpRequestFactory.buildPostRequest(connectorUrl, null);
    return parseNoResponse(httpRequest);
  }

  @Override
  public CompletableFuture<List<ConnectorPlugin>> connectorPluginsAsync() throws IOException {
    GenericUrl url = connectorsPluginsUrl();
    HttpRequest httpRequest = this.httpRequestFactory.buildGetRequest(url);
    return parseAs(httpRequest, CONNECTOR_PLUGIN_TYPE);
  }

  @Override
  public CompletableFuture<ValidateResponse> validateAsync(String name, ImmutableMap<Object, Object> config) throws IOException {
    GenericUrl url = connectorsPluginsUrl(
        name,
        "config",
        "validate"
    );
    HttpRequest httpRequest = this.httpRequestFactory.buildPutRequest(url, new JsonHttpContent(jsonFactory, config));
    return parseAs(httpRequest, ValidateResponse.class);
  }

  @Override
  public CompletableFuture<ServerInfo> serverInfoAsync() throws IOException {
    HttpRequest httpRequest = this.httpRequestFactory.buildGetRequest(this.baseUrl);
    return parseAs(httpRequest, ServerInfo.class);
  }

}
