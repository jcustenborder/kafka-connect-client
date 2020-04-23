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

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Authenticator;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.time.Duration;

public class KafkaConnectClientFactory {
  private HttpUrl.Builder baseUrlBuilder = new HttpUrl.Builder()
      .host("localhost")
      .port(8083)
      .scheme("http");

  private String username;
  private String password;
  private ObjectMapper objectMapper = new ObjectMapper();

  private KafkaConnectClientImpl createClientImpl() {
    OkHttpClient client = new OkHttpClient.Builder()
        .callTimeout(Duration.ofSeconds(30))
        .connectTimeout(Duration.ofSeconds(30))
        .writeTimeout(Duration.ofSeconds(30))
        .readTimeout(Duration.ofSeconds(30))
        .authenticator(new Authenticator() {
          @Nullable
          @Override
          public Request authenticate(@Nullable Route route, @NotNull Response response) throws IOException {
            return null;
          }
        })
        .build();
    HttpUrl httpUrl = this.baseUrlBuilder.build();
    return new KafkaConnectClientImpl(httpUrl, client,
        username, password, objectMapper);
  }

  /**
   * Method is used to create an async binding.
   *
   * @return
   */
  public AsyncKafkaConnectClient createAsyncClient() {
    return createClientImpl();
  }

  /**
   * Method is used to create a kafka connect client.
   *
   * @return
   */
  public KafkaConnectClient createClient() {
    return createClientImpl();
  }

  /**
   * The host running the Kafka Connect api.
   *
   * @param host
   */
  public KafkaConnectClientFactory host(String host) {
    this.baseUrlBuilder.host(host);
    return this;
  }

  /**
   * The port on the host running the Kafka Connect api.
   *
   * @param port
   */
  public KafkaConnectClientFactory port(int port) {
    this.baseUrlBuilder.port(port);
    return this;
  }

  public KafkaConnectClientFactory scheme(String scheme) {
    this.baseUrlBuilder.scheme(scheme);
    return this;
  }

  public KafkaConnectClientFactory username(String username) {
    this.username = username;
    return this;
  }

  public KafkaConnectClientFactory password(String password) {
    this.password = password;
    return this;
  }
}
