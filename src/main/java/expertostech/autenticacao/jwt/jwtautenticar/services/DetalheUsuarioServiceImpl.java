package expertostech.autenticacao.jwt.jwtautenticar.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class DetalheUsuarioServiceImpl implements AuthenticationProvider {

	@Value("${authentication.username}")
	private String username;

	@Value("${authentication.password}")
	private String password;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String name = authentication.getName();
		String password = authentication.getCredentials().toString();

		// Your custom authentication logic here
		if (name.equals(username) && password.equals(password)) {
			Authentication auth = new UsernamePasswordAuthenticationToken(name,
					password);
			return auth;
		}
		throw new UsernameNotFoundException("Usuário [" + username + "] não encontrado");
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
