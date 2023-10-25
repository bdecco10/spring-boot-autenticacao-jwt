package expertostech.autenticacao.jwt.exception.dto;

import lombok.*;
import org.springframework.http.HttpStatus;
@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private String description;
    private HttpStatus status;
    private Throwable exception;
}
