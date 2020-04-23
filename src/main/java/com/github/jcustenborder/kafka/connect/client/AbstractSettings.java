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


import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


abstract class AbstractSettings {

  @Value.Parameter
  @Value.Default
  public String host() {
    return "localhost";
  }

  @Value.Parameter
  @Value.Default
  public int port() {
    return 8083;
  }

  @Value.Parameter
  @Value.Default
  public String scheme() {
    return "http";
  }

  @Value.Parameter
  @Value.Default
  @Nullable
  public String username() {
    return null;
  }

  @Value.Parameter
  @Value.Default
  @Nullable
  public String password() {
    return null;
  }


  @Value.Parameter
  @Value.Default
  public Duration callTimeout() {
    return Duration.ofSeconds(30);
  }

  @Value.Parameter
  @Value.Default
  public Duration connectTimeout() {
    return Duration.ofSeconds(30);
  }

  @Value.Parameter
  @Value.Default
  public Duration writeTimeout() {
    return Duration.ofSeconds(30);
  }

  @Value.Parameter
  @Value.Default
  public Duration readTimeout() {
    return Duration.ofSeconds(30);
  }

  @Value.Parameter
  @Value.Default
  public int retries() {
    return 10;
  }

  @Value.Parameter
  @Value.Default
  public Duration delayBetweenAttempts() {
    return Duration.ofSeconds(5);
  }

  @Value.Parameter
  @Value.Default
  public OkHttpClient client() {
    return new OkHttpClient.Builder()
        .callTimeout(callTimeout())
        .connectTimeout(connectTimeout())
        .writeTimeout(writeTimeout())
        .readTimeout(readTimeout())
        .build();
  }

  @Value.Derived
  HttpUrl baseUrl() {
    return new HttpUrl.Builder()
        .host(host())
        .port(port())
        .scheme(scheme())
        .build();
  }

  @Nullable
  @Value.Derived
  String credentials() {
    String result;
    if (null == username() || username().isEmpty()) {
      result = null;
    } else {
      result = Credentials.basic(username(), password());
    }
    return result;
  }

  @Value.Derived
  boolean hasCredentials() {
    return (null != username() && !username().isEmpty());
  }

  @Value.Derived
  ObjectMapper objectMapper() {
    return new ObjectMapper();
  }

  @Value.Derived
  long delayAsMilliseconds() {
    return delayBetweenAttempts().toMillis();
  }



  @Value.Parameter
  @Value.Default
  public ScheduledExecutorService scheduler() {
    return Executors.newScheduledThreadPool(1);
  }

  @Value.Parameter
  @Value.Default
  public boolean shutdownSchedulerOnClose() {
    return true;
  }

//  public interface Builder {
//    Builder host(String host);
//
//    Builder port(int port);
//
//    Builder scheme(String scheme);
//
//    Builder username(String username);
//
//    Builder password(String password);
//
//    Builder callTimeout(Duration callTimeout);
//
//    Builder connectTimeout(Duration connectTimeout);
//
//    Builder writeTimeout(Duration writeTimeout);
//
//    Builder readTimeout(Duration readTimeout);
//
//    Builder retries(int retries);
//
//    Builder delayBetweenAttempts(Duration delayBetweenAttempts);
//
//    Builder client(OkHttpClient client);
//
//    Builder scheduler(ScheduledExecutorService scheduler);
//
//    Builder shutdownSchedulerOnClose(boolean shutdownSchedulerOnClose);
//
//    AbstractSettings build();
//  }

}
