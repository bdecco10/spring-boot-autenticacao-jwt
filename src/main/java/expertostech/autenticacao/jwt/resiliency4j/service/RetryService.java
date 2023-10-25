package expertostech.autenticacao.jwt.resiliency4j.service;

import expertostech.autenticacao.jwt.logger.service.LogService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

@Service
@AllArgsConstructor
public class RetryService {

    private final LogService logService;

    @Retry(name = "funciona1")
    public String retryableOperation() {
        // Simulando uma operação que pode falhar
        if (Math.random() < 0.5) {
            logService.logError("Operação com error",new ResourceAccessException("504"));
            throw new ResourceAccessException("504");
        }
       return "Operação bem-sucedida";
    }

    @Retry(name = "funciona2")
    @CircuitBreaker(name = "operacaoBreaker")
    public String retryableOperationCircuitBreaker() {
        // Simulando uma operação que pode falhar
        String message = "Operação bem-sucedida";
        logService.logInfo(message);

        return message;
    }
}
