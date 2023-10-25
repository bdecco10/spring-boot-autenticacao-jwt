package expertostech.autenticacao.jwt.resiliency4j.controller;

import expertostech.autenticacao.jwt.resiliency4j.service.RetryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@Tag(name = "Retry", description = "API que teste retry feito pela aplicacao")
public class RetryController {

    @Autowired
    private RetryService retryService;

    @GetMapping("/retryable")
    @Operation(summary = "Obter string de sucesso", description = "Retorna uma string de sucesso.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso proibido"),
            @ApiResponse(responseCode = "404", description = "Recursos não encontrados")
    })
    public String retryableEndpoint() {
        return retryService.retryableOperation();
    }

}
