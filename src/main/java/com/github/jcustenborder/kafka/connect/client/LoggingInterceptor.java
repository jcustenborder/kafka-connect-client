package com.github.jcustenborder.kafka.connect.client;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

class LoggingInterceptor implements Interceptor {
  private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);

  @NotNull
  @Override
  public Response intercept(@NotNull Chain chain) throws IOException {
    Request request = chain.request();
    Response response = chain.proceed(request);
    if(log.isTraceEnabled()) {
      log.trace("request = '{}' response='{}'", request, response);
    }
    return response;
  }
}
