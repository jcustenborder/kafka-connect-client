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