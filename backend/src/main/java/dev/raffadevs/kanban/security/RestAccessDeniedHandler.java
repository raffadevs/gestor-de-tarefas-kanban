package dev.raffadevs.kanban.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.raffadevs.kanban.common.ErroResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Resposta JSON consistente para 403, no mesmo formato de
 * {@link dev.raffadevs.kanban.common.GlobalExceptionHandler} — que não é
 * acionado aqui porque a exceção ocorre no filtro de segurança, antes do
 * DispatcherServlet.
 */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public RestAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                        AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErroResponse erro = ErroResponse.de(HttpStatus.FORBIDDEN.value(), "Você não tem permissão para esta ação");
        // Grava no OutputStream (Jackson usa UTF-8), não no Writer — que, sem um
        // charset explícito no content type, cai para ISO-8859-1 e corrompe acentos.
        objectMapper.writeValue(response.getOutputStream(), erro);
    }
}
