package expertostech.autenticacao.jwt.basetoken;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ContextConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Define o escopo de instância como PER_CLASS (singleton)
@TestPropertySource(locations = "classpath:bootstrap-test.yml")
public abstract class BaseJwtTokenTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    private String extractedToken;
    @BeforeAll
    public void setUp() throws Exception {
        String username = "centric_plm";
        String password = "centric_plm";

        // Simular a autenticação bem-sucedida
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                new User(username, password, new ArrayList<>()),
                null,
                new ArrayList<>()
        );
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        // Simular a chamada para autenticar e obter o token
        String responseBody = mockMvc.perform(MockMvcRequestBuilders
                        .post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extrair o token do corpo da resposta
        extractedToken = responseBody.replace("\"", "");
    }

    public String getExtractedToken() {
        return extractedToken;
    }
}
