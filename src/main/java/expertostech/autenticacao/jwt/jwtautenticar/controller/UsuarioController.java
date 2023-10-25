package expertostech.autenticacao.jwt.jwtautenticar.controller;

import expertostech.autenticacao.jwt.jwtautenticar.model.UsuarioModel;
import expertostech.autenticacao.jwt.refreshscope.UpdateInfoRealTime;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
@RestController
@RequestMapping("/api/usuario")
@Tag(name = "Usuario", description = "API que retorna o usuario cadastrado")
public class UsuarioController {

    private final PasswordEncoder encoder;

    public UsuarioController(PasswordEncoder encoder) {
		this.encoder = encoder;
	}

    @Autowired
    private UpdateInfoRealTime UpdateInfoRealTime ;
	@GetMapping("/listarTodos")
    @Operation(summary = "Obter usuário por ID", description = "Retorna um usuário com base no ID fornecido.")
    @ApiResponse(responseCode = "200", description = "Lista de exemplos retornada com sucesso")
    public ResponseEntity<List<UsuarioModel>> listarTodos() {
        UsuarioModel usuarioModel = new UsuarioModel();
        List<UsuarioModel>user = new ArrayList();
        usuarioModel.setLogin(UpdateInfoRealTime.getMinhaChave());
        user.add(usuarioModel);
        return ResponseEntity.ok(user);
    }

}
