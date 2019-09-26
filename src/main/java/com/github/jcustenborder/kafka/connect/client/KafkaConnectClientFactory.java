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

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KafkaConnectClientFactory {
  private HttpTransport httpTransport = new NetHttpTransport();
  private static final JsonFactory JSON_FACTORY = new JacksonFactory();
  private GenericUrl baseUrl = new GenericUrl("http://localhost:8083");
  private ExecutorService executorService = Executors.newSingleThreadExecutor();

  public ExecutorService executorService() {
    return this.executorService;
  }

  public void executorService(ExecutorService executorService) {
    this.executorService = executorService;
  }

  public HttpTransport httpTransport() {
    return this.httpTransport;
  }

  public void httpTransport(HttpTransport httpTransport) {
    this.httpTransport = httpTransport;
  }


  private KafkaConnectClientImpl createClientImpl() {
    HttpRequestFactory httpRequestFactory = this.httpTransport.createRequestFactory(httpRequest -> {
      httpRequest.setFollowRedirects(false);
      httpRequest.setCurlLoggingEnabled(true);
      httpRequest.setParser(new JsonObjectParser(JSON_FACTORY));
      httpRequest.setThrowExceptionOnExecuteError(false);
      httpRequest.getHeaders().setAcceptEncoding(null);
      httpRequest.setSuppressUserAgentSuffix(true);
      httpRequest.getHeaders().setUserAgent("kafka-connect-client");
    });
    return new KafkaConnectClientImpl(
        executorService, this.baseUrl,
        httpRequestFactory,
        this.JSON_FACTORY
    );
  }

  public AsyncKafkaConnectClient createAsyncClient() {
    return createClientImpl();
  }

  public KafkaConnectClient createClient() {
    return createClientImpl();
  }

  public void host(String host) {
    this.baseUrl.setHost(host);
  }

  public void port(int port) {
    this.baseUrl.setPort(port);
  }

  public String host() {
    return this.baseUrl.getHost();
  }

  public int port() {
    return this.baseUrl.getPort();
  }

}
