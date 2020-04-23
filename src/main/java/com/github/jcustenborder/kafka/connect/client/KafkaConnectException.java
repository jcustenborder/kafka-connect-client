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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jcustenborder.kafka.connect.client.model.ErrorResponse;

import java.io.IOException;

/**
 * Exception that is thrown when the connect rest api returns an error message.
 */
public class KafkaConnectException extends IOException {

  @JsonProperty("error_code")
  private Integer errorCode;
  @JsonProperty("message")
  private String message;

  public KafkaConnectException() {
  }

  KafkaConnectException(ErrorResponse error) {
    this(error.errorCode(), error.message());
  }

  KafkaConnectException(Integer errorCode, String message) {
    this.errorCode = errorCode;
    this.message = message;
  }

  /**
   * Error code that was returned from the REST API.
   *
   * @return
   */
  public Integer errorCode() {
    return this.errorCode;
  }

  /**
   * Error message that was returned from the REST API.
   *
   * @return
   */
  @Override
  public String getMessage() {
    return String.format("Error %s: %s", this.errorCode, this.message);
  }
}
