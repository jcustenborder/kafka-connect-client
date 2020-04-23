[![Maven Central](https://img.shields.io/maven-central/v/com.github.jcustenborder.kafka/kafka-connect-client.svg)](https://search.maven.org/search?q=g:com.github.jcustenborder.kafka%20AND%20a:kafka-connect-client&core=gav)
# Introduction

This project provides Java bindings for the Kafka Connect REST API. The idea is to have an easily mockable interface 
that allows applications to be created to manage Kafka Connect clusters without parsing individual json 
responses. The client has support to retry when it receives a 409 rebalancing error.

# Example

```java
import com.github.jcustenborder.kafka.connect.client.KafkaConnectClientFactoryOld;
import com.github.jcustenborder.kafka.connect.client.KafkaConnectClient;
import com.github.jcustenborder.kafka.connect.client.model.CreateOrUpdateConnectorResponse;
import java.util.LinkedHashMap;
import java.util.Map;

class Example {
    KafkaConnectClient client;

    public static void main(String... args) throws Exception {
        try(KafkaConnectClient client = KafkaConnectClient.builder()
            .host("kafka-connect-01.example.com")
            .port(8083)
            .createClient()) {
                    
            Map<String, String> config = new LinkedHashMap<>();
            config.put("connector.class", "org.apache.kafka.connect.file.FileStreamSinkConnector");
            config.put("tasks.max", "1");
            config.put("topics", "test-topic");
            config.put("name", "foo");
            CreateConnectorResponse response = client.createOrUpdate("foo", config);

            //Restart the connector
            client.restart("foo");

            //Restart the first task 
            client.restart("foo", 1);
 
            //Remove the connector
            client.delete("foo");
        }
    } 
}
```