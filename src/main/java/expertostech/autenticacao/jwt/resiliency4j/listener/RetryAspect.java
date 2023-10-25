package expertostech.autenticacao.jwt.resiliency4j.listener;

import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.retry.event.RetryEvent;

//@Aspect
//@Component
public class RetryAspect {

	private final RetryRegistry retryRegistry;
	 
	public RetryAspect(RetryRegistry retryRegistry) {
        this.retryRegistry = retryRegistry;
    }

	//@Pointcut("@annotation(retry)")
	public void retryAnnotationPointcut(Retry retry) {
	}

	//@AfterThrowing(pointcut = "retryAnnotationPointcut(retry)", throwing = "exception")
	public void onException(Retry retry, Throwable exception) {
		io.github.resilience4j.retry.Retry retryInstance = (io.github.resilience4j.retry.Retry) retryRegistry.retry(retry.name());
        retryInstance.getEventPublisher().onRetry(event -> {
        	if (event.getEventType() == RetryEvent.Type.RETRY) {
    			System.out.println("Evento de Retry - Tentativa: " + event.getNumberOfRetryAttempts());
    			if (event.getLastThrowable() != null) {
    				System.out.println("Exceção: " + event.getLastThrowable().getMessage());
    			}
    		}
        });
	}

}
