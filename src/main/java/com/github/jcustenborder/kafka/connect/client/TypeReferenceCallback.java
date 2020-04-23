package com.github.jcustenborder.kafka.connect.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;

class TypeReferenceCallback<T> extends JsonCallback<TypeReference<T>, T> {
  TypeReferenceCallback(OkHttpClient client, ObjectMapper objectMapper, CompletableFuture<T> futureResult, int maxAttempts, ScheduledExecutorService scheduler, long delay, TypeReference<T> type) {
    super(client, objectMapper, futureResult, maxAttempts, scheduler, delay, type);
  }

  @Override
  protected T parse(Response response, TypeReference<T> type) throws IOException {
    return parseTypeReference(response, type);
  }
}