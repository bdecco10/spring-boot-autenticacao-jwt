package expertostech.autenticacao.jwt.kafka.deserializer;

import expertostech.autenticacao.jwt.kafka.dto.KafkaDTO;
import expertostech.autenticacao.jwt.logger.service.LogService;
import lombok.AllArgsConstructor;
import org.apache.kafka.common.serialization.Deserializer;

import java.nio.charset.StandardCharsets;

@AllArgsConstructor
public class CustomBaseHandlingDeserializer<T> implements Deserializer<T> {
     private final Deserializer<T> innerDeserializer ;
    private final LogService logService;
    @Override
    public T deserialize(String topic, byte[] data) {
        String json = new String(data, StandardCharsets.UTF_8);
        try {
            T response = innerDeserializer.deserialize(topic, data);
            return response;
        } catch (Exception ex) {
            // Lançar uma exceção para parar as retentativas
            logService.logError(json, ex);
            return (T) new KafkaDTO();
        }
    }
}
