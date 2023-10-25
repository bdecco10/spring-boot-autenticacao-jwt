package expertostech.autenticacao.jwt.resiliency4j.listener;

import org.springframework.stereotype.Component;

import io.github.resilience4j.core.EventConsumer;
import io.github.resilience4j.retry.event.RetryEvent;
import io.github.resilience4j.retry.event.RetryOnRetryEvent;

@Component
public class RetryEventListener  implements EventConsumer<RetryOnRetryEvent> {

	@Override
	public void consumeEvent(RetryOnRetryEvent event) {
		if (event.getEventType() == RetryEvent.Type.RETRY) {
			System.out.println("Evento de Retry - Tentativa: " + event.getNumberOfRetryAttempts());
			if (event.getLastThrowable() != null) {
				System.out.println("Exceção: " + event.getLastThrowable().getMessage());
			}
		}
	}

}
