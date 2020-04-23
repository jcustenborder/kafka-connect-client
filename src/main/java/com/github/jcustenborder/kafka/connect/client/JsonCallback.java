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

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.jcustenborder.kafka.connect.client.model.ErrorResponse;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

abstract class JsonCallback<V, T> implements Callback {
  private static final Logger log = LoggerFactory.getLogger(ClassCallback.class);
  private static final String JSON = "application/json";
  protected final AbstractSettings settings;
  private final AtomicInteger attempts;
  private final CompletableFuture<T> futureResult;
  private final V type;

  JsonCallback(AbstractSettings settings, CompletableFuture<T> futureResult, V type) {
    this.settings = settings;
    this.futureResult = futureResult;
    this.attempts = new AtomicInteger(0);
    this.type = type;
  }

  @Override
  public void onFailure(@NotNull Call call, @NotNull IOException e) {
    log.warn("executeRequest() - Request failure. request = '{}'", call.request(), e);
    futureResult.completeExceptionally(e);
  }

  protected <V> V parseClass(Response response, Class<V> type) throws IOException {
    String body = response.body().string();
    if (log.isTraceEnabled()) {
      log.trace("parseAs() - response: \n{}", body);
    }
    return this.settings.objectMapper().readValue(body, type);
  }

  protected <V> V parseTypeReference(Response response, TypeReference<V> type) throws IOException {
    String body = response.body().string();
    if (log.isTraceEnabled()) {
      log.trace("parseAs() - response: \n{}", body);
    }
    return this.settings.objectMapper().readValue(body, type);
  }

  protected abstract T parse(Response response, V type) throws IOException;


  @Override
  public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
    if (response.isSuccessful()) {
      T result;
      if (Void.class.equals(type)) {
        result = null;
      } else {
        result = parse(response, type);
      }
      this.futureResult.complete(result);
    } else {
      int currentAttempt = this.attempts.incrementAndGet();

      if (409 == response.code() && this.attempts.get() < this.settings.retries()) {
        log.info("Retrying {} due to rebalancing. Attempt {} of {}.", call.request(), currentAttempt, this.settings.retries());
        response.close();
        this.settings.scheduler().schedule(() -> {
          log.trace("onResponse() - Scheduling request.");
          newCall(call.request());
        }, this.settings.delayAsMilliseconds(), TimeUnit.MILLISECONDS);
        return;
      }

      String contentType = response.header("Content-Type");
      if (JSON.equals(contentType)) {
        ErrorResponse error = parseClass(response, ErrorResponse.class);
        this.futureResult.completeExceptionally(new KafkaConnectException(error));
      } else {
        this.futureResult.completeExceptionally(new IllegalStateException("Why am i here"));
      }

    }
    response.close();
  }

  public void newCall(Request request) {
    this.settings.client().newCall(request).enqueue(this);
  }
}
