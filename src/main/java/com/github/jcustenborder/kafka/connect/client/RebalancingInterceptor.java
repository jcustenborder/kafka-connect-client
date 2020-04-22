package com.github.jcustenborder.kafka.connect.client;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

class RebalancingInterceptor implements Interceptor {
  private static final Logger log = LoggerFactory.getLogger(RebalancingInterceptor.class);
  final int maxAttempts;

  RebalancingInterceptor(int maxAttempts) {
    this.maxAttempts = maxAttempts;
  }

  @NotNull
  @Override
  public Response intercept(@NotNull Chain chain) throws IOException {
    Request request = chain.request();
    Response response = chain.proceed(request);
    int statusCode = response.code();
    int attempts = 1;
    while (409 == statusCode && attempts <= this.maxAttempts) {
      log.trace("intercept() - Rebalancing attempt {} of {}", attempts, this.maxAttempts);
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {

      } finally {
        attempts++;
      }
      response = chain.proceed(request);
      statusCode = response.code();
    }
    return response;
  }
}
