package expertostech.autenticacao.jwt.swagger.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Minha API")
                        .description("Descrição da API"))
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"));
    }
}
