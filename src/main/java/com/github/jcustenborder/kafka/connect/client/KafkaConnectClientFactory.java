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

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KafkaConnectClientFactory {
  private HttpTransport httpTransport = new NetHttpTransport();
  private GenericUrl baseUrl = new GenericUrl("http://localhost:8083");
  private ExecutorService executorService = Executors.newSingleThreadExecutor();
  private boolean shutdownTransportOnClose = true;
  private boolean shutdownExecutorServiceOnClose = true;
  private String username;
  private String password;

  /**
   * ExecutorService for all requests.
   * @return
   */
  public ExecutorService executorService() {
    return this.executorService;
  }

  /**
   * Set the ExecutorService for the client.
   * @param executorService
   */
  public void executorService(ExecutorService executorService) {
    this.executorService = executorService;
  }

  /**
   * HTTP transport to use
   * @return
   */
  public HttpTransport httpTransport() {
    return this.httpTransport;
  }

  /**
   * Configure the HttpTransport
   * @param httpTransport
   */
  public void httpTransport(HttpTransport httpTransport) {
    this.httpTransport = httpTransport;
  }

  /**
   * Flag to determine if the transport should be shutdown when close is called on the client.
   * @return
   */
  public boolean shutdownTransportOnClose() {
    return this.shutdownTransportOnClose;
  }

  /**
   * Flag to determine if the transport should be shutdown when close is called on the client.
   * @param shutdownTransportOnClose
   */
  public void shutdownTransportOnClose(boolean shutdownTransportOnClose) {
    this.shutdownTransportOnClose = shutdownTransportOnClose;
  }

  /**
   * Flag to determine if the ExecutorService should be shutdown when close is called on the client.
   * @return
   */
  public boolean shutdownExecutorServiceOnClose() {
    return this.shutdownExecutorServiceOnClose;
  }

  /**
   * Flag to determine if the ExecutorService should be shutdown when close is called on the client.
   * @param shutdownExecutorServiceOnClose
   */
  public void shutdownExecutorServiceOnClose(boolean shutdownExecutorServiceOnClose) {
    this.shutdownExecutorServiceOnClose = shutdownExecutorServiceOnClose;
  }

  private KafkaConnectClientImpl createClientImpl() {
    return new KafkaConnectClientImpl(
        executorService, this.baseUrl,
        httpTransport,
        shutdownTransportOnClose,
        shutdownExecutorServiceOnClose,
        username, password);
  }

  /**
   * Method is used to create an async binding.
   * @return
   */
  public AsyncKafkaConnectClient createAsyncClient() {
    return createClientImpl();
  }

  /**
   * Method is used to create a kafka connect client.
   * @return
   */
  public KafkaConnectClient createClient() {
    return createClientImpl();
  }

  /**
   * The host running the Kafka Connect api.
   * @param host
   */
  public void host(String host) {
    this.baseUrl.setHost(host);
  }

  /**
   * The host running the Kafka Connect api.
   * @return
   */
  public String host() {
    return this.baseUrl.getHost();
  }

  /**
   * The port on the host running the Kafka Connect api.
   * @param port
   */
  public void port(int port) {
    this.baseUrl.setPort(port);
  }

  /**
   * The port on the host running the Kafka Connect api.
   * @return
   */
  public int port() {
    return this.baseUrl.getPort();
  }

  public void scheme(String scheme) {
    this.baseUrl.setScheme(scheme);
  }

  public String scheme() {
    return this.baseUrl.getScheme();
  }

  public String username() {
    return this.username;
  }

  public void username(String username) {
    this.username = username;
  }

  public String password() {
    return this.password;
  }

  public void password(String password) {
    this.password = password;
  }
}
