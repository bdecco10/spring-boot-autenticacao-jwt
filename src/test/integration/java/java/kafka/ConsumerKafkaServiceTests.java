package kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import expertostech.autenticacao.jwt.AutenticacaoJwtApplication;
import expertostech.autenticacao.jwt.kafka.dto.KafkaDTO;
import expertostech.autenticacao.jwt.kafka.service.KafkaService;
import kafka.utils.CustomKafkaTestUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AutenticacaoJwtApplication.class)
@DirtiesContext
public class ConsumerKafkaServiceTests extends CustomKafkaTestUtils{

    @Autowired
    private KafkaService kafkaService;

    @Autowired
    private KafkaProperties kafkaProperties;

    private Consumer<String, String> consumer;

    @Autowired
    private KafkaAdmin kafkaAdmin;

    @Test
    public void testEnviarMensagemKafkaParaOmesmoTopic() throws JsonProcessingException {


        Map<String, Object> consumerProps = kafkaProperties.buildConsumerProperties();
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "kafkaConsumerGroup"); // Use a propriedade personalizada aqui
        consumer = new KafkaConsumer<>(consumerProps, new StringDeserializer(), new StringDeserializer());
        consumer.subscribe(Collections.singleton("meu-topico"));

        // Envie uma mensagem para o Kafka usando o KafkaTemplate
        KafkaDTO dto = new KafkaDTO();
        dto.setCampo1("testando o kafka!");
        dto.setCampo2(10);
        kafkaService.enviarMensagemParaKafka(dto, "meu-topico");

        // Aguarde até que a mensagem seja consumida pelo consumidor
        ConsumerRecord<String, String> singleRecord = KafkaTestUtils.getSingleRecord(consumer, "meu-topico");

        JsonNode jsonNode = objectMapper.readTree(singleRecord.value());

        String campo1 = jsonNode.get("campo1").asText();
        assertThat(singleRecord).isNotNull();
        assertThat(campo1).isEqualTo("testando o kafka!");
    }

    @Test
    public void testEnviarMensagemKafkaParaOutroTopic() throws JsonProcessingException {
        Map<String, Object> consumerProps = kafkaProperties.buildConsumerProperties();
        consumer = new KafkaConsumer<>(consumerProps, new StringDeserializer(), new StringDeserializer());
        consumer.subscribe(Collections.singleton("meu-novo-topico"));

        // Envie uma mensagem para o Kafka usando o KafkaTemplate
        String mensagem = "testando o kafka!";
        kafkaService.enviarMensagemParaKafka(mensagem, "meu-novo-topico");

        // Aguarde até que a mensagem seja consumida pelo consumidor
        ConsumerRecord<String, String> singleRecord = KafkaTestUtils.getSingleRecord(consumer, "meu-novo-topico");
        assertThat(singleRecord).isNotNull();
        assertThat(singleRecord.value()).isEqualTo(mensagem);
    }


    @Test
    public void testEnviarVariasMensagemKafkaParaUmTopic() throws JsonProcessingException {

        Map<String, Object> consumerProps = kafkaProperties.buildConsumerProperties();
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "kafkaConsumerGroupMAisUm"); // Use a propriedade personalizada aqui
        consumer = new KafkaConsumer<>(consumerProps, new StringDeserializer(), new StringDeserializer());
        consumer.subscribe(Collections.singleton("meu-mais-novo-topico"));

        // Envie uma mensagem para o Kafka usando o KafkaTemplate
        KafkaDTO dto = new KafkaDTO();
        dto.setCampo1("testando o kafka 10!");
        dto.setCampo2(10);
        kafkaService.enviarMensagemParaKafka(dto, "meu-mais-novo-topico");
        dto.setCampo1("testando o kafka 11!");
        dto.setCampo2(11);
        kafkaService.enviarMensagemParaKafka(dto, "meu-mais-novo-topico");
        dto.setCampo1("testando o kafka 12!");
        dto.setCampo2(12);
        kafkaService.enviarMensagemParaKafka(dto, "meu-mais-novo-topico");

        // Aguarde até que a mensagem seja consumida pelo consumidor
        List<ConsumerRecord<String, String>> allRecords = getAllRecordsFromTopic(consumer, "meu-mais-novo-topico");
        assertThat(allRecords.size()).isEqualTo(3);

    }

}
