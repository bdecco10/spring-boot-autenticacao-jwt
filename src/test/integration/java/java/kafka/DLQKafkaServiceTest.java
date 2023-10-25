package kafka;

import expertostech.autenticacao.jwt.AutenticacaoJwtApplication;
import expertostech.autenticacao.jwt.kafka.config.KafkaProducerConfig;
import expertostech.autenticacao.jwt.kafka.dto.KafkaDTO;
import expertostech.autenticacao.jwt.kafka.service.KafkaService;
import kafka.utils.CustomKafkaTestUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {AutenticacaoJwtApplication.class, KafkaProducerConfig.class})
@DirtiesContext
@ActiveProfiles("integration-test")
class DLQKafkaServiceTest extends CustomKafkaTestUtils {

    @Autowired
    private KafkaService kafkaService;
    @Autowired
    private KafkaProperties kafkaProperties;
    @KafkaListener(groupId = "my-group-test",topics = "meu-primeiro-topico",containerFactory = "kafkaListenerContainerFactory")
    public void receiveMessage(@Payload List<KafkaDTO> kafkaDTO) {
        throw new RuntimeException("Testando DLQ");
    }

    @Test
    public void testDlqProcessing() throws Exception {
        KafkaDTO dto = new KafkaDTO();
        dto.setCampo1("Minha mensagem de teste");
        dto.setCampo2(10);
        String topic = "meu-primeiro-topico";
        String dlqTopic = "meu-primeiro-topico-dlq";

        // Produzir uma mensagem no tópico principal
        kafkaService.enviarMensagemParaKafka(dto,topic);

        Map<String, Object> consumerProps = kafkaProperties.buildConsumerProperties();

        // Crie um KafkaConsumer para consumir mensagens da DLQ de teste
        try (Consumer<String, String> consumer = new KafkaConsumer<>(consumerProps)) {
            consumer.subscribe(Collections.singleton(dlqTopic));
            int numMessagesInDLQ = 0;
            while (numMessagesInDLQ < 1) {
                Iterable<ConsumerRecord<String, String>> records = consumer.poll(100);
                for (ConsumerRecord<String, String> record : records) {
                    // Processar mensagens da DLQ de teste
                    // Aqui, você pode verificar o conteúdo da mensagem e fazer as asserções necessárias
                    System.out.println("Received message from DLQ: " + record.value());
                    numMessagesInDLQ++;
                }
            }

            // Verifique se as mensagens estão na DLQ de teste
            assertEquals(1, numMessagesInDLQ);
        }
    }
}
