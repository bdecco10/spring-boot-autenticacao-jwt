package expertostech.autenticacao.jwt.kafka.util;

import org.apache.kafka.common.serialization.Serializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;
import java.util.Map;
public class StringOrJsonSerializer implements Serializer<Object> {
    private final JsonSerializer<Object> jsonSerializer = new JsonSerializer<>();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // Não é necessário configurar nada para este serializador
    }

    @Override
    public byte[] serialize(String topic, Object data) {
        Assert.notNull(data, "Data must not be null.");

        if (data instanceof String) {
            return ((String) data).getBytes(StandardCharsets.UTF_8);
        } else {
            // Se não for uma string simples, use o JsonSerializer padrão
            return jsonSerializer.serialize(topic, data);
        }
    }

    @Override
    public void close() {
        // Não é necessário fechar nada para este serializador
    }
}
