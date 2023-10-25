package expertostech.autenticacao.jwt.resiliency4j.listener;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerEvent;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnStateTransitionEvent;
import io.github.resilience4j.core.EventConsumer;
import io.github.resilience4j.core.EventPublisher;
import io.github.resilience4j.core.registry.EntryAddedEvent;

public class CircuitBreakerEventListener implements EventConsumer<EntryAddedEvent<CircuitBreaker>> {
    @Override
    public void consumeEvent(EntryAddedEvent<CircuitBreaker> event) {
        CircuitBreaker circuitBreaker = event.getAddedEntry();
        EventPublisher eventPublisher = circuitBreaker.getEventPublisher();
        eventPublisher.onEvent(stateTransitionEvent -> {
            if (stateTransitionEvent instanceof CircuitBreakerOnStateTransitionEvent) {
                CircuitBreakerOnStateTransitionEvent cbStateTransitionEvent =
                        (CircuitBreakerOnStateTransitionEvent) stateTransitionEvent;

                if (cbStateTransitionEvent.getStateTransition().getToState() == CircuitBreaker.State.OPEN) {
                    // Ação genérica para tratamento de abertura do circuito
                    System.out.println("Circuit Breaker aberto! Realizar ações de recuperação genéricas...");
                }
            }
        });
    }
}
