package expertostech.autenticacao.jwt.kafka.producer;

import expertostech.autenticacao.jwt.logger.service.LogService;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaProperties kafkaProperties;
    private final LogService logService;

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate, KafkaProperties kafkaProperties, LogService logService) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaProperties = kafkaProperties;
        this.logService = logService;
    }
    private void criarTopicoComPoliticaDeRetencao(String nomeDoTopico, int particoes, short replicacao, long retencaoMs) {
        NewTopic newTopic = new NewTopic(nomeDoTopico, particoes, replicacao);
        newTopic.configs(Collections.singletonMap("retention.ms", Long.toString(retencaoMs)));
        AdminClient adminClient = AdminClient.create(kafkaProperties.buildConsumerProperties());
        adminClient.createTopics(Collections.singleton(newTopic));
    }
    public void sendMessage(Object message, String topicName) {
        logService.logInfo(message+" topic:"+topicName);
        criarTopicoComPoliticaDeRetencao(topicName, 1, (short) 1, 60000);
            kafkaTemplate.send(topicName, message);
    }
}