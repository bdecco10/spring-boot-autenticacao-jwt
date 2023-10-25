package risiliency4j;

import expertostech.autenticacao.jwt.AutenticacaoJwtApplication;
import expertostech.autenticacao.jwt.resiliency4j.service.RetryService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.ResourceAccessException;

import java.time.Duration;

import static org.mockito.Mockito.doThrow;

@SpringBootTest(classes = AutenticacaoJwtApplication.class)
@ActiveProfiles("test")
class CircuitBreakerApplicationTests {

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;
    @SpyBean
    private RetryService myService;

    private CircuitBreakerConfig circuitBreakerConfig;

    @BeforeEach
    public void setup() {
        circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)  // Defina o valor desejado
                .waitDurationInOpenState(Duration.ofMillis(45))  // Defina o valor desejado
                .slidingWindowSize(6)  // Defina o valor desejado
                .permittedNumberOfCallsInHalfOpenState(100)  // Defina o valor desejado
                .recordExceptions(Exception.class)
                .automaticTransitionFromOpenToHalfOpenEnabled(true) // Ativa a transição automática
                .build();

    }

    @Test
    @DisplayName("Testando o retry")
    public void testRetryForceOpen() {

        try {
            myService.retryableOperation();
        } catch (Exception ex) {
            Assertions.assertEquals(ResourceAccessException.class, ex);
        }
    }

    @Test
    @DisplayName("Testando o Circuit Breaker force Open")
    public void testCircuitBreakerForceOpen() {

        CircuitBreaker circuit = circuitBreakerRegistry.circuitBreaker("operacaoBreaker", circuitBreakerConfig);
        circuit.transitionToOpenState();
        try {
            myService.retryableOperationCircuitBreaker();  // Chame o serviço protegido pelo circuit breaker
        } catch (Exception ex) {
            Assertions.assertEquals(CircuitBreaker.State.OPEN, circuit.getState());
        }
    }

    @Test
    @DisplayName("Testando o Circuit Breaker force Open and Closed")
    public void testCircuitBreakerForceOpenEndClosed() {

        CircuitBreaker circuit = circuitBreakerRegistry.circuitBreaker("operacaoBreaker", circuitBreakerConfig);
        circuit.transitionToOpenState();
        try {
            myService.retryableOperationCircuitBreaker();  // Chame o serviço protegido pelo circuit breaker
        } catch (Exception ex) {
            Assertions.assertEquals(CircuitBreaker.State.OPEN, circuit.getState());
            circuit.transitionToClosedState();
        }

        myService.retryableOperationCircuitBreaker();  // Chame o serviço protegido pelo circuit breaker
        Assertions.assertEquals(CircuitBreaker.State.CLOSED, circuit.getState());

    }

    @Test
    @DisplayName("Testando o Circuit Breaker Open failureRateThreshold up 50% in 7 request")
    public void testCircuitBreakerOpenFailureRateThreshold50Porcent() {

        CircuitBreaker circuit = circuitBreakerRegistry.circuitBreaker("operacaoBreaker", circuitBreakerConfig);
        doThrow(new RuntimeException("Simulated Exception")).when(myService).retryableOperationCircuitBreaker();

        for (int i = 0; i <= 7; i++) {
            try {
                myService.retryableOperationCircuitBreaker();  // Chame o serviço protegido pelo circuit breaker
            } catch (Exception ex) {
            }
        }
        Assertions.assertEquals(CircuitBreaker.State.OPEN, circuit.getState());
    }

    @Test
    @DisplayName("Testando o Circuit Breaker Open failureRateThreshold up 50% in 7 request with delay 1 minuto status HalOpen")
    public void testCircuitBreakerOpenFailureRateThreshold50PorcentAndHalf() throws InterruptedException {

        CircuitBreaker circuit = circuitBreakerRegistry.circuitBreaker("operacaoBreaker", circuitBreakerConfig);
        doThrow(new RuntimeException("Simulated Exception")).when(myService).retryableOperationCircuitBreaker();

        for (int i = 0; i <= 7; i++) {
            try {
                myService.retryableOperationCircuitBreaker();  // Chame o serviço protegido pelo circuit breaker
            } catch (Exception ex) {
            }
        }
        Thread.sleep(Duration.ofMinutes(1).toMillis());
        Assertions.assertEquals(CircuitBreaker.State.HALF_OPEN, circuit.getState());
    }

}
