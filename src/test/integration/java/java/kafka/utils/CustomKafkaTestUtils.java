package kafka.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CountDownLatch;

@EmbeddedKafka(
        partitions = 1,
        brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"}
)
@TestPropertySource(locations = "classpath:bootstrap-integration-test.yml")
@ActiveProfiles("integration-test")
public abstract class CustomKafkaTestUtils {

    public ObjectMapper objectMapper;
    // Usando CountDownLatch para aguardar a inicialização do Kafka Embedded
    private final CountDownLatch embeddedKafkaInitialized = new CountDownLatch(1);


    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
        embeddedKafkaInitialized.countDown();
        waitForConsumerToProcessMessage();
    }

    public <K, V> List<ConsumerRecord<K, V>> getAllRecordsFromTopic(Consumer<K, V> consumer, String topic) {
        consumer.subscribe(Collections.singleton(topic));

        List<ConsumerRecord<K, V>> allRecords = new ArrayList<>();

        while (true) {
            ConsumerRecords<K, V> records = consumer.poll(Duration.ofMillis(100));
            if (records.isEmpty()) {
                break;
            }

            for (ConsumerRecord<K, V> record : records) {
                allRecords.add(record);
            }
        }

        consumer.close();
        return allRecords;
    }

    public <K, V> List<ConsumerRecord<K, V>> getAllRecordsFromTopic(String bootstrapServers, String groupId, String topic) {
        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        try (Consumer<K, V> consumer = new KafkaConsumer<>(consumerProps)) {
            return getAllRecordsFromTopic(consumer, topic);
        }
    }

    public void waitForConsumerToProcessMessage() {
        // Aguarde um curto período de tempo para que o consumidor processe a mensagem
        try {
            // Aguardar até que o Kafka Embedded esteja pronto
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
