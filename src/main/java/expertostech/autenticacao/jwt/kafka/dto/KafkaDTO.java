package expertostech.autenticacao.jwt.kafka.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.ANY, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class KafkaDTO implements Serializable {
    private String campo1;
    private Integer campo2;
}
