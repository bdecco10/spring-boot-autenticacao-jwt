package expertostech.autenticacao.jwt.refreshscope;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * Anotacao @RefresScope responsavel por pegar em realtime, qualquer alteracao no config-server
 * para funcionar precisa chamar o path da aplicacao/actuator/refresh/ e depois executar a
 * chamada de API novamente para recuperar o novo valor
 * Spring Cloud (config-serve)
 */
@Component
@RefreshScope
public class UpdateInfoRealTime {

    @Value("${minha.chave}")
    private String minhaChave;

    public String getMinhaChave() {
        return minhaChave;
    }
}
