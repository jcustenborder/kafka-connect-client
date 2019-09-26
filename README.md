# Introduction

This project provides Java bindings for the Kafka Connect REST API. The idea is to have an easily mockable interface 
that allows applications to be created to manage Kafka Connect clusters without parsing individual json 
responses.

# Example

```java
import com.github.jcustenborder.kafka.connect.client.KafkaConnectClientFactory;
import com.github.jcustenborder.kafka.connect.client.KafkaConnectClient;
import com.github.jcustenborder.kafka.connect.client.model.CreateOrUpdateConnectorResponse;
import java.util.LinkedHashMap;
import java.util.Map;

class Example {
    KafkaConnectClient client;

    void createClient() {
      KafkaConnectClientFactory clientFactory;
      clientFactory = new KafkaConnectClientFactory();
      clientFactory.host("kafka-connect-01.example.com");
      clientFactory.port(8083);
      client = clientFactory.createClient();
    }

    void createConnector() {
        Map<String, String> config = new LinkedHashMap<>();
        config.put("connector.class", "org.apache.kafka.connect.file.FileStreamSinkConnector");
        config.put("tasks.max", "1");
        config.put("topics", "test-topic");
        config.put("name", "foo");
        CreateOrUpdateConnectorResponse response = client.createOrUpdate("foo", config);
    }
  
    void restartConnector() {
      client.restart("foo");
    }   
}
```