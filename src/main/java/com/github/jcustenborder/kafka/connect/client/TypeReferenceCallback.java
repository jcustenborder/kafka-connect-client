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
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

class TypeReferenceCallback<T> extends JsonCallback<TypeReference<T>, T> {
  TypeReferenceCallback(AbstractSettings settings, CompletableFuture<T> futureResult, TypeReference<T> type) {
    super(settings, futureResult, type);
  }

  @Override
  protected T parse(Response response, TypeReference<T> type) throws IOException {
    return parseTypeReference(response, type);
  }
}