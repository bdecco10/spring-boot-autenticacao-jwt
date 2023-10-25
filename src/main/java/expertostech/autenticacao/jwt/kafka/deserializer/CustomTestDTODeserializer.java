package expertostech.autenticacao.jwt.kafka.deserializer;

import expertostech.autenticacao.jwt.kafka.dto.TesteDTO;
import expertostech.autenticacao.jwt.kafka.producer.KafkaProducerService;
import expertostech.autenticacao.jwt.logger.service.LogService;
import lombok.AllArgsConstructor;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class CustomTestDTODeserializer extends CustomBaseHandlingDeserializer<TesteDTO>  {
    @Autowired
    public CustomTestDTODeserializer(Deserializer<TesteDTO> innerDeserializer, LogService logService) {
        super(innerDeserializer, logService);
    }
}
