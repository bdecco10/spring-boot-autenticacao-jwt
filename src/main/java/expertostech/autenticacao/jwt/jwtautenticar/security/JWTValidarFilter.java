package expertostech.autenticacao.jwt.jwtautenticar.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import expertostech.autenticacao.jwt.exception.dto.ErrorResponse;
import expertostech.autenticacao.jwt.logger.service.LogService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class JWTValidarFilter extends BasicAuthenticationFilter {

    public static final String HEADER_ATRIBUTO = "Authorization";
    public static final String ATRIBUTO_PREFIXO = "Bearer ";
    private final LogService logService;

    private final ObjectMapper objectMapper;

    public JWTValidarFilter(AuthenticationManager authenticationManager, LogService logService, ObjectMapper objectMapper) {
        super(authenticationManager);
        this.logService = logService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request,
                                 HttpServletResponse response,
                                 FilterChain chain) throws IOException, ServletException {

        String atributo = request.getHeader(HEADER_ATRIBUTO);

        if (atributo == null) {
            chain.doFilter(request, response);
            return;
        }

        if (!atributo.startsWith(ATRIBUTO_PREFIXO)) {
            chain.doFilter(request, response);
            return;
        }
        try {
            String token = atributo.replace(ATRIBUTO_PREFIXO, "");
            UsernamePasswordAuthenticationToken authenticationToken = getAuthenticationToken(token);

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            chain.doFilter(request, response);
        } catch (TokenExpiredException e) {
            // Captura a exceção TokenExpiredException
            logService.logError(e.getMessage(),e);
            handleAuthenticationException(HttpServletResponse.SC_FORBIDDEN, response, e);
            return;
        } catch (AuthenticationException e) {
            // Outras exceções de autenticação
            logService.logError(e.getMessage(),e);
            handleAuthenticationException(HttpServletResponse.SC_UNAUTHORIZED, response, e);
            return;
        } catch (SignatureVerificationException e) {
            // Outras exceções de autenticação
            logService.logError(e.getMessage(),e);
            handleAuthenticationException(HttpServletResponse.SC_UNAUTHORIZED, response, e);
            return;
        }
    }

    public UsernamePasswordAuthenticationToken getAuthenticationToken(String token) {
        String usuario = JWT.require(Algorithm.HMAC512(JWTAutenticarFilter.TOKEN_SENHA))
                .build()
                .verify(token)
                .getSubject();

        if (usuario == null) {
            return null;
        }

        return new UsernamePasswordAuthenticationToken(usuario, null, new ArrayList<>());
    }


    private void handleAuthenticationException(Integer statusCode, HttpServletResponse response, Exception ex) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json;charset=UTF-8");

        ErrorResponse errorResponse = new ErrorResponse("Ocorreu um erro .", ex.getMessage(), HttpStatus.valueOf(statusCode), ex);
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
    }
}
