package expertostech.autenticacao.jwt.kafka.consumer;

import expertostech.autenticacao.jwt.kafka.dto.TesteDTO;
import expertostech.autenticacao.jwt.kafka.dto.KafkaDTO;
import expertostech.autenticacao.jwt.kafka.util.KafkaDTOValidator;
import expertostech.autenticacao.jwt.logger.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    private String consumedMessage; // Armazena a mensagem consumida
    @Autowired
    private LogService logService;

    @KafkaListener(groupId = "my-group", topics = "meu-topico", containerFactory = "kafkaListenerContainerFactory")
    public void receiveMessage(@Payload KafkaDTO kafkaDTO) {

        KafkaDTOValidator.validate(kafkaDTO);

        // Processar a mensagem recebida
            System.out.println("Mensagem recebida: " + kafkaDTO);
            this.consumedMessage = kafkaDTO.getCampo1();
            logService.logInfo(kafkaDTO);
    }
    @KafkaListener(groupId = "my-group-erro", topics = "meu-topico-teste", containerFactory = "kafkaListenerContainerFactoryTest")
    public void receiveMessageError(@Payload TesteDTO error) {

        // Processar a mensagem recebida
        System.out.println("Mensagem recebida de error: " + error);
        logService.logInfo(error.getData());
    }
    // Método para recuperar a mensagem consumida
    public String getConsumedMessage() {
        return consumedMessage;
    }
}