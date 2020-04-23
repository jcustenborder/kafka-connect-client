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
import okhttp3.OkHttpClient;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;

class ClassCallback<T> extends JsonCallback<Class<T>, T> {
  ClassCallback(OkHttpClient client, ObjectMapper objectMapper, CompletableFuture<T> futureResult, int maxAttempts, ScheduledExecutorService scheduler, long delay, Class<T> cls) {
    super(client, objectMapper, futureResult, maxAttempts, scheduler, delay, cls);
  }

  @Override
  protected T parse(Response response, Class<T> type) throws IOException {
    return parseClass(response, type);
  }
}