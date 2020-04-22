package com.github.jcustenborder.kafka.connect.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class MockRequestExtension implements ParameterResolver {

  ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    Optional<LoadMockResponse> loadMockResponse = parameterContext.findAnnotation(LoadMockResponse.class);
    if (!loadMockResponse.isPresent()) {
      return false;
    }

    return true;
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    Optional<LoadMockResponse> optionalLoadMockResponse = parameterContext.findAnnotation(LoadMockResponse.class);
    if (!optionalLoadMockResponse.isPresent()) {
      return null;
    }

    LoadMockResponse loadMockResponse = optionalLoadMockResponse.get();


    MockResponse mockResponse = new MockResponse();
//    mockResponse.setResponseCode(loadMockResponse.code());
    InputStream inputStream = this.getClass().getResourceAsStream(loadMockResponse.path());

    if (null == inputStream) {
      throw new ParameterResolutionException(
          String.format("File %s not found", loadMockResponse.path())
      );
    }

    Class<?> type = parameterContext.getParameter().getType();

    try {
      return this.objectMapper.readValue(
          inputStream,
          type
      );
    } catch (IOException e) {
      throw new ParameterResolutionException("exception while loading " + loadMockResponse.path(), e);
    }
  }
}
