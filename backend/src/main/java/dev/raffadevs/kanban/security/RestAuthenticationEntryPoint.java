package dev.raffadevs.kanban.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.raffadevs.kanban.common.ErroResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Sem este bean, o Spring Security usa Http403ForbiddenEntryPoint por padrão
 * quando nem formLogin nem httpBasic estão configurados — ou seja, uma
 * requisição sem token (não autenticada) devolveria 403 em vez de 401. Para
 * uma API stateless com JWT, 401 é o código correto: "autentique-se" versus
 * "você não tem permissão" (403, tratado por {@link RestAccessDeniedHandler}).
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public RestAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                          AuthenticationException authException) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErroResponse erro = ErroResponse.de(HttpStatus.UNAUTHORIZED.value(), "Autenticação necessária");
        // Grava no OutputStream (Jackson usa UTF-8), não no Writer — que, sem um
        // charset explícito no content type, cai para ISO-8859-1 e corrompe acentos.
        objectMapper.writeValue(response.getOutputStream(), erro);
    }
}
