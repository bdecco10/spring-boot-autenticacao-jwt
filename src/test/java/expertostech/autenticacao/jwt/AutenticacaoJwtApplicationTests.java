package expertostech.autenticacao.jwt;

import expertostech.autenticacao.jwt.basetoken.BaseJwtTokenTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class AutenticacaoJwtApplicationTests extends BaseJwtTokenTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void listarUsuariosTest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/usuario/listarTodos")
                        .header("Authorization", "Bearer " + getExtractedToken()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void listarUsuarios2Test() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/usuario/listarTodos")
                        .header("Authorization", "Bearer " + getExtractedToken()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
