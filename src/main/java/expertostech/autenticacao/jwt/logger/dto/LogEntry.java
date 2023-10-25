package expertostech.autenticacao.jwt.logger.dto;

import expertostech.autenticacao.jwt.logger.dto.ErrorException;
import expertostech.autenticacao.jwt.logger.dto.Request;
import expertostech.autenticacao.jwt.logger.dto.Response;
import lombok.*;

import java.time.LocalDateTime;

@Data
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Builder
public class LogEntry {

    private LocalDateTime timestamp;
    private Request request;
    private Response response;
    private ErrorException error;

    public LogEntry() {
        this.timestamp = LocalDateTime.now();
    }

}
