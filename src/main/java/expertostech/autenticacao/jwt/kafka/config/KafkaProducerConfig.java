package expertostech.autenticacao.jwt.kafka.config;

import expertostech.autenticacao.jwt.kafka.deserializer.CustomKafkaDTODeserializer;
import expertostech.autenticacao.jwt.kafka.deserializer.CustomTestDTODeserializer;
import expertostech.autenticacao.jwt.kafka.dto.KafkaDTO;
import expertostech.autenticacao.jwt.kafka.dto.TesteDTO;
import expertostech.autenticacao.jwt.kafka.producer.KafkaProducerService;
import expertostech.autenticacao.jwt.kafka.util.StringOrJsonSerializer;
import expertostech.autenticacao.jwt.logger.service.LogService;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.Collections;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Autowired
    private KafkaProperties kafkaProperties;

    @Autowired
    private LogService logService;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = kafkaProperties.buildConsumerProperties();
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringOrJsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }


    @Bean
    public ConsumerFactory<String, KafkaDTO> consumerFactory() {
        Map<String, Object> configProps = kafkaProperties.buildConsumerProperties();
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CustomKafkaDTODeserializer.class.getName());
        // Configure as trusted packages
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "expertostech.autenticacao.jwt.kafka.dto");

        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), new CustomKafkaDTODeserializer(new JsonDeserializer<>(KafkaDTO.class), logService));
    }

    @Bean
    public ConsumerFactory<String, TesteDTO> consumerFactoryTest() {
        Map<String, Object> configProps = kafkaProperties.buildConsumerProperties();
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CustomTestDTODeserializer.class.getName());
        // Configure as trusted packages
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "expertostech.autenticacao.jwt.kafka.dto");

        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), new CustomTestDTODeserializer(new JsonDeserializer<>(TesteDTO.class), logService));
    }

    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(KafkaTemplate<String, Object> kafkaTemplate) {
        return new DeadLetterPublishingRecoverer(kafkaTemplate,
                (record, exception) -> {
                    String mainQueueName = record.topic();
                    NewTopic topic = new NewTopic(mainQueueName + "-dlq", 1, (short) 1);
                    AdminClient adminClient = AdminClient.create(kafkaProperties.buildConsumerProperties());
                    adminClient.createTopics(Collections.singleton(topic));
                    logService.logError("Msg enviada para a DLQ: " + mainQueueName + "-dlq", exception);
                    return new TopicPartition(mainQueueName + "-dlq", 1);
                });
    }

    @Bean
    public SeekToCurrentErrorHandler seekToCurrentErrorHandler(
            DeadLetterPublishingRecoverer recoverer, KafkaTemplate<String, Object> kafkaTemplate) {
        SeekToCurrentErrorHandler errorHandler = new SeekToCurrentErrorHandler(recoverer);
        errorHandler.setBackOffFunction((record, exception) -> {
            // Personalize o atraso com base na exceção ou no registro, se necessário.
            // Aqui, estamos usando um atraso fixo de 2 segundos para todas as retentativas.
            return new FixedBackOff(2000, 3); // 2 segundos de intervalo, até 3 retentativas.
        });
        errorHandler.setAckAfterHandle(true); // Isso garante que as mensagens sejam confirmadas após o tratamento da exceção.
        errorHandler.setCommitRecovered(true); // Isso garante que as mensagens que falharam não serão reprocessadas indefinidamente

        return errorHandler;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaDTO> kafkaListenerContainerFactory(KafkaTemplate<String, Object> kafkaTemplate,
                                                                                                   DeadLetterPublishingRecoverer recoverer,
                                                                                                   SeekToCurrentErrorHandler seekToCurrentErrorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, KafkaDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        //factory.setBatchListener(true);
        factory.setErrorHandler(seekToCurrentErrorHandler);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TesteDTO> kafkaListenerContainerFactoryTest(KafkaTemplate<String, Object> kafkaTemplate,
                                                                                                       DeadLetterPublishingRecoverer recoverer,
                                                                                                       SeekToCurrentErrorHandler seekToCurrentErrorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, TesteDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryTest());
        //factory.setBatchListener(true);
        factory.setErrorHandler(seekToCurrentErrorHandler);
        return factory;
    }
}
