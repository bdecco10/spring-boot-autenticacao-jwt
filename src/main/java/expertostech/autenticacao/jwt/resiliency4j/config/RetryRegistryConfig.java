package expertostech.autenticacao.jwt.resiliency4j.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import expertostech.autenticacao.jwt.resiliency4j.listener.CircuitBreakerEventListener;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.core.EventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import expertostech.autenticacao.jwt.resiliency4j.listener.RetryEventListener;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;

@Configuration
public class RetryRegistryConfig {

	@Autowired
	private RetryEventListener retryEventListener;

	@Bean
	public RetryRegistry retryRegistry() {
		RetryRegistry retryRegistry = RetryRegistry.of(RetryConfig.custom()
				.maxAttempts(3)
				.intervalFunction(IntervalFunction.ofExponentialBackoff(Duration.ofMillis(100), 2, Duration.ofMillis(500)))
				.retryExceptions(HttpServerErrorException.BadGateway.class,HttpServerErrorException.GatewayTimeout.class)
				.retryOnException(exception -> {
					if (exception instanceof ResourceAccessException) {
						boolean result502Or504 = verifyError(exception.getMessage());
						if (result502Or504) {
							return true;
						}
					}
					return false;
				})
				.build());
		Retry retry = retryRegistry.retry("funciona");
		retry.getEventPublisher().onRetry(retryEventListener);
		return retryRegistry;
	}

	@Bean
	public CircuitBreakerRegistry circuitBreakerRegistry() {
		CircuitBreakerConfig configRegistration = CircuitBreakerConfig.custom()
				.failureRateThreshold(30)
				.waitDurationInOpenState(Duration.ofSeconds(30))
				.slidingWindowSize(100)
				.permittedNumberOfCallsInHalfOpenState(10)
				.recordExceptions(Exception.class)
				.build();
		Map<String,CircuitBreakerConfig> map = new HashMap<>();
		map.put("operacaoBreaker",configRegistration);

		CircuitBreakerRegistry circuitBreakerRegistry = CircuitBreakerRegistry.of(map);
		// Adicionar o ouvinte de eventos de CircuitBreaker
		EventPublisher eventPublisher = circuitBreakerRegistry.getEventPublisher();
		eventPublisher.onEvent(new CircuitBreakerEventListener());

		return circuitBreakerRegistry;
	}
	private boolean verifyError(String message){
		return message.contains(String.valueOf(HttpStatus.BAD_GATEWAY.value())) || message.contains(String.valueOf(HttpStatus.GATEWAY_TIMEOUT.value()));
	}
	
}
