package expertostech.autenticacao.jwt.logger.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

@Data
@ToString
@EqualsAndHashCode
@Builder
public class Request {
    private Map<String,Object> headers;
    private Object payload;
}
