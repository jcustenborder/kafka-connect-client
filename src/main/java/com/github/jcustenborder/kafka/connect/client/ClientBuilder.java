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

import okhttp3.OkHttpClient;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;

public interface ClientBuilder<T> {
  T host(String host);

  T port(int port);

  T scheme(String scheme);

  T username(String username);

  T password(String password);

  T callTimeout(Duration callTimeout);

  T connectTimeout(Duration connectTimeout);

  T writeTimeout(Duration writeTimeout);

  T readTimeout(Duration readTimeout);

  T retries(int retries);

  T delayBetweenAttempts(Duration delayBetweenAttempts);

  T client(OkHttpClient client);

  T scheduler(ScheduledExecutorService scheduler);

  T shutdownSchedulerOnClose(boolean shutdownSchedulerOnClose);
}
