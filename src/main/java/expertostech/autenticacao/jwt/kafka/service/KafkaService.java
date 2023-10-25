package expertostech.autenticacao.jwt.kafka.service;

import expertostech.autenticacao.jwt.kafka.dto.KafkaDTO;
import expertostech.autenticacao.jwt.kafka.producer.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {
    private final KafkaProducerService producerService;

    @Autowired
    public KafkaService(KafkaProducerService producerService) {
        this.producerService = producerService;
    }

    public void enviarMensagemParaKafka(Object mensage, String topic) {
        producerService.sendMessage(mensage, topic);
    }
}
