package expertostech.autenticacao.jwt.logger.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
@Builder
public class ErrorException extends Exception{
    private String details;
    private String type;
}
