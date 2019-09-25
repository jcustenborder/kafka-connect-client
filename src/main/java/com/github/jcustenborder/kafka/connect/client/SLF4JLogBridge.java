/**
 * Copyright © 2017 Jeremy Custenborder (jcustenborder@gmail.com)
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

import com.google.api.client.http.HttpTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

class SLF4JLogBridge {
  private static final Logger log = LoggerFactory.getLogger(SLF4JLogBridge.class);

  static {
    java.util.logging.Logger logger = java.util.logging.Logger.getLogger(HttpTransport.class.getName());
    logger.setLevel(Level.CONFIG);

    Consumer<LogRecord> trace = logRecord -> log.trace(logRecord.getMessage());
    Consumer<LogRecord> debug = logRecord -> log.debug(logRecord.getMessage());
    Consumer<LogRecord> info = logRecord -> log.info(logRecord.getMessage());
    Consumer<LogRecord> warn = logRecord -> log.warn(logRecord.getMessage());
    Consumer<LogRecord> error = logRecord -> log.error(logRecord.getMessage());

    Map<Level, Consumer<LogRecord>> levelMappings = new HashMap<>();
    levelMappings.put(Level.FINEST, trace);
    levelMappings.put(Level.FINE, debug);
    levelMappings.put(Level.CONFIG, debug);
    levelMappings.put(Level.INFO, info);
    levelMappings.put(Level.WARNING, warn);
    levelMappings.put(Level.SEVERE, error);


    logger.addHandler(new Handler() {
      @Override
      public void publish(LogRecord record) {
        Consumer<LogRecord> consumer = levelMappings.getOrDefault(record.getLevel(), trace);
        consumer.accept(record);
      }

      @Override
      public void flush() {

      }

      @Override
      public void close() throws SecurityException {

      }
    });
  }

  static void init() {

  }
}
