package kafka;

import expertostech.autenticacao.jwt.AutenticacaoJwtApplication;
import expertostech.autenticacao.jwt.kafka.consumer.KafkaConsumerService;
import expertostech.autenticacao.jwt.kafka.dto.KafkaDTO;
import expertostech.autenticacao.jwt.kafka.service.KafkaService;
import kafka.utils.CustomKafkaTestUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AutenticacaoJwtApplication.class)
@DirtiesContext
public class ProducerKafkaServiceTests extends CustomKafkaTestUtils{

    @Autowired
    private KafkaService kafkaService;

    @Autowired
    private KafkaConsumerService kafkaConsumerService;

    @Autowired
    private KafkaProperties kafkaProperties;

    private Consumer<String, String> consumer;

    @Test
    public void testEnviarEreceberMensagemKafka() {
        // Envie uma mensagem para o Kafka usando o KafkaTemplate
        Map<String, Object> consumerProps = kafkaProperties.buildConsumerProperties();
        consumer = new KafkaConsumer<>(consumerProps, new StringDeserializer(), new StringDeserializer());

        KafkaDTO dto = new KafkaDTO();
        dto.setCampo1("testando o kafka!");
        dto.setCampo2(10);
        kafkaService.enviarMensagemParaKafka(dto, "meu-topico");
        waitForConsumerToProcessMessage();
        assertThat(kafkaConsumerService.getConsumedMessage()).isEqualTo("testando o kafka!");
    }

}
