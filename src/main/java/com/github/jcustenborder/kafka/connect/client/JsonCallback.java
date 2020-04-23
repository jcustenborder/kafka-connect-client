package com.github.jcustenborder.kafka.connect.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jcustenborder.kafka.connect.client.model.ErrorResponse;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

abstract class JsonCallback<V, T> implements Callback {
  private static final Logger log = LoggerFactory.getLogger(ClassCallback.class);
  private final OkHttpClient client;
  private final ObjectMapper objectMapper;
  private final CompletableFuture<T> futureResult;
  private final AtomicInteger attempts;
  private final int maxAttempts;
  private final ScheduledExecutorService scheduler;
  private final long delay;
  private final V type;

  JsonCallback(OkHttpClient client, ObjectMapper objectMapper, CompletableFuture<T> futureResult, int maxAttempts, ScheduledExecutorService scheduler, long delay, V type) {
    this.client = client;
    this.objectMapper = objectMapper;
    this.futureResult = futureResult;
    this.maxAttempts = maxAttempts;
    this.scheduler = scheduler;
    this.delay = delay;
    this.attempts = new AtomicInteger(0);
    this.type = type;
  }

  @Override
  public void onFailure(@NotNull Call call, @NotNull IOException e) {
    log.warn("executeRequest() - Request failure. request = '{}'", call.request(), e);
    futureResult.completeExceptionally(e);
  }

  final String JSON = "application/json";

  protected <V> V parseClass(Response response, Class<V> type) throws IOException {
    String body = response.body().string();
    if (log.isTraceEnabled()) {
      log.trace("parseAs() - response: \n{}", body);
    }
    return this.objectMapper.readValue(body, type);
  }

  protected <V> V parseTypeReference(Response response, TypeReference<V> type) throws IOException {
    String body = response.body().string();
    if (log.isTraceEnabled()) {
      log.trace("parseAs() - response: \n{}", body);
    }
    return this.objectMapper.readValue(body, type);
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

      if (409 == response.code() && this.attempts.get() < this.maxAttempts) {
        log.info("Retrying {} due to rebalancing. Attempt {} of {}.", call.request(), currentAttempt, this.maxAttempts);
        response.close();
        this.scheduler.schedule(() -> {
          log.trace("onResponse() - Scheduling request.");
          newCall(call.request());
        }, this.delay, TimeUnit.MILLISECONDS);
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
    this.client.newCall(request).enqueue(this);
  }
}
