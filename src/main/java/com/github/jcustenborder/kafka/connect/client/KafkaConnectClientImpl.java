/**
 * Copyright © 2017 Jeremy Custenborder (jcustenborder@gmail.com)
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

class KafkaConnectClientImpl implements KafkaConnectClient {
  private static final Logger log = LoggerFactory.getLogger(KafkaConnectClientImpl.class);

  static {
    SLF4JLogBridge.init();
  }


  final GenericUrl baseUrl;
  final HttpRequestFactory httpRequestFactory;
  final JsonFactory jsonFactory;

  KafkaConnectClientImpl(GenericUrl baseUrl, HttpRequestFactory httpRequestFactory, JsonFactory jsonFactory) {
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

  @Override
  public List<String> connectors() throws IOException {
    GenericUrl url = connectorsUrl();
    log.trace("connectors() - url = '{}'", url);
    HttpRequest request = this.httpRequestFactory.buildGetRequest(url);
    HttpResponse httpResponse = request.execute();
    List<String> result = parseAs(httpResponse, List.class);
    return result;
  }

  void checkConnectorConfig(Map<String, String> config) {
    Preconditions.checkNotNull("config", "config cannot be null");
    Preconditions.checkState(config.containsKey("connector.class"), "connector.class must be specified.");
    Preconditions.checkState(config.containsKey("tasks.max"), "tasks.max must be specified.");
  }

  @Override
  public CreateOrUpdateConnectorResponse createOrUpdate(String name, Map<String, String> config) throws IOException {
    Preconditions.checkNotNull(name, "name cannot be null");
    checkConnectorConfig(config);
    GenericUrl connectorUrl = connectorsUrl(name, "config");
    JsonHttpContent content = new JsonHttpContent(this.jsonFactory, config);
    HttpRequest httpRequest = this.httpRequestFactory.buildPutRequest(connectorUrl, content);
    HttpResponse httpResponse = httpRequest.execute();
    CreateOrUpdateConnectorResponse result = parseAs(httpResponse, CreateOrUpdateConnectorResponse.class);
    return result;
  }

  @Override
  public GetConnectorResponse get(String name) throws IOException {
    Preconditions.checkNotNull(name, "name cannot be null");
    GenericUrl connectorUrl = connectorsUrl(name);
    HttpRequest httpRequest = this.httpRequestFactory.buildGetRequest(connectorUrl);
    HttpResponse httpResponse = httpRequest.execute();
    GetConnectorResponse result = parseAs(httpResponse, GetConnectorResponse.class);
    return result;
  }

  @Override
  public Map<String, String> config(String name) throws IOException {
    GenericUrl url = connectorsUrl(name, "config");
    log.trace("connectors() - url = '{}'", url);
    HttpRequest request = this.httpRequestFactory.buildGetRequest(url);
    HttpResponse httpResponse = request.execute();
    Map<String, String> result = parseAs(httpResponse, Map.class);
    return result;
  }

  @Override
  public void delete(String name) throws IOException {
    Preconditions.checkNotNull(name, "name cannot be null");
    GenericUrl connectorUrl = connectorsUrl(name);

    HttpRequest httpRequest = this.httpRequestFactory.buildDeleteRequest(connectorUrl);
    HttpResponse httpResponse = httpRequest.execute();
    parseNoResponse(httpResponse);
  }

  @Override
  public void restart(String name) throws IOException {
    GenericUrl connectorUrl = connectorsUrl(name, "restart");
    HttpRequest httpRequest = this.httpRequestFactory.buildPostRequest(connectorUrl, null);
    HttpResponse httpResponse = httpRequest.execute();
    parseNoResponse(httpResponse);
  }

  @Override
  public void pause(String name) throws IOException {
    GenericUrl connectorUrl = connectorsUrl(name, "pause");
    HttpRequest httpRequest = this.httpRequestFactory.buildPutRequest(connectorUrl, null);
    HttpResponse httpResponse = httpRequest.execute();
    parseNoResponse(httpResponse);
  }

  @Override
  public void resume(String name) throws IOException {
    GenericUrl connectorUrl = connectorsUrl(name, "resume");
    HttpRequest httpRequest = this.httpRequestFactory.buildPutRequest(connectorUrl, null);
    HttpResponse httpResponse = httpRequest.execute();
    parseNoResponse(httpResponse);
  }

  @Override
  public ConnectorStatusResponse status(String name) throws IOException {
    GenericUrl connectorUrl = connectorsUrl(name, "status");
    HttpRequest httpRequest = this.httpRequestFactory.buildGetRequest(connectorUrl);
    HttpResponse httpResponse = httpRequest.execute();
    return parseAs(httpResponse, ConnectorStatusResponse.class);
  }

  @Override
  public TaskStatusResponse status(String name, int taskId) throws IOException {
    GenericUrl connectorUrl = connectorsUrl(name, "tasks", Integer.toString(taskId), "status");
    HttpRequest httpRequest = this.httpRequestFactory.buildGetRequest(connectorUrl);
    HttpResponse httpResponse = httpRequest.execute();
    return parseAs(httpResponse, TaskStatusResponse.class);
  }

  @Override
  public void restart(String name, int taskId) throws IOException {
    GenericUrl connectorUrl = connectorsUrl(name, "tasks", Integer.toString(taskId), "restart");
    HttpRequest httpRequest = this.httpRequestFactory.buildPostRequest(connectorUrl, null);
    HttpResponse httpResponse = httpRequest.execute();
    parseNoResponse(httpResponse);
  }

  static final TypeToken<List<ConnectorPlugin>> CONNECTOR_PLUGIN_TYPE = new TypeToken<List<ConnectorPlugin>>() {
  };

  @Override
  public List<ConnectorPlugin> connectorPlugins() throws IOException {
    GenericUrl url = connectorsPluginsUrl();
    HttpRequest httpRequest = this.httpRequestFactory.buildGetRequest(url);
    HttpResponse httpResponse = httpRequest.execute();
    return parseAs(httpResponse, CONNECTOR_PLUGIN_TYPE);
  }

  @Override
  public ValidateResponse validate(String name, ImmutableMap<Object, Object> config) throws IOException {
    GenericUrl url = connectorsPluginsUrl(
        name,
        "config",
        "validate"
    );
    HttpRequest httpRequest = this.httpRequestFactory.buildPutRequest(url, new JsonHttpContent(jsonFactory, config));
    HttpResponse httpResponse = httpRequest.execute();
    return parseAs(httpResponse, ValidateResponse.class);
  }

  @Override
  public ServerInfo serverInfo() throws IOException {
    HttpRequest httpRequest = this.httpRequestFactory.buildGetRequest(this.baseUrl);
    HttpResponse httpResponse = httpRequest.execute();
    return parseAs(httpResponse, ServerInfo.class);
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

  private void parseNoResponse(HttpResponse httpResponse) throws IOException {
    checkAndRaiseException(httpResponse);
  }

  private <T> T parseAs(HttpResponse httpResponse, TypeToken<T> token) throws IOException {
    checkAndRaiseException(httpResponse);
    Type type = token.getType();
    Object result = httpResponse.parseAs(type);
    return (T) result;
  }

  private <T> T parseAs(HttpResponse httpResponse, Class<T> cls) throws IOException {
    checkAndRaiseException(httpResponse);
    return httpResponse.parseAs(cls);
  }
}
