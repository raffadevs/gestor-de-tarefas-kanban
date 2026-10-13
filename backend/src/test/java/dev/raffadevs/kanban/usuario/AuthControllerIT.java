package dev.raffadevs.kanban.usuario;

import com.fasterxml.jackson.databind.JsonNode;
import dev.raffadevs.kanban.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

class AuthControllerIT extends IntegrationTestBase {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void deveRegistrarLogarERenovarToken() {
        ResponseEntity<JsonNode> registro = registrar("ana@exemplo.com", "senha12345", "Ana");
        assertThat(registro.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(registro.getBody().get("accessToken").asText()).isNotBlank();
        String refreshToken = registro.getBody().get("refreshToken").asText();

        ResponseEntity<JsonNode> login = login("ana@exemplo.com", "senha12345");
        assertThat(login.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<JsonNode> renovacao = restTemplate.postForEntity(
                "/api/auth/refresh", jsonRequest("""
                        {"refreshToken":"%s"}
                        """.formatted(refreshToken)), JsonNode.class);
        assertThat(renovacao.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(renovacao.getBody().get("refreshToken").asText()).isNotEqualTo(refreshToken);
    }

    @Test
    void refreshTokenUsadoDuasVezesEhRejeitadoNaSegunda() {
        ResponseEntity<JsonNode> registro = registrar("bruno@exemplo.com", "senha12345", "Bruno");
        String refreshToken = registro.getBody().get("refreshToken").asText();

        ResponseEntity<JsonNode> primeira = restTemplate.postForEntity(
                "/api/auth/refresh", jsonRequest("""
                        {"refreshToken":"%s"}
                        """.formatted(refreshToken)), JsonNode.class);
        assertThat(primeira.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<JsonNode> segunda = restTemplate.postForEntity(
                "/api/auth/refresh", jsonRequest("""
                        {"refreshToken":"%s"}
                        """.formatted(refreshToken)), JsonNode.class);
        assertThat(segunda.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void deveRejeitarSenhaIncorreta() {
        registrar("carla@exemplo.com", "senha12345", "Carla");

        ResponseEntity<JsonNode> login = login("carla@exemplo.com", "senha-errada");
        assertThat(login.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void deveRejeitarEmailDuplicado() {
        registrar("duplicado@exemplo.com", "senha12345", "Primeira");

        ResponseEntity<JsonNode> segundoRegistro = registrar("duplicado@exemplo.com", "outrasenha", "Segunda");
        assertThat(segundoRegistro.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void endpointProtegidoRejeitaRequisicaoSemToken() {
        ResponseEntity<JsonNode> resposta = restTemplate.getForEntity("/api/quadros", JsonNode.class);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity<JsonNode> registrar(String email, String senha, String nome) {
        return restTemplate.postForEntity("/api/auth/registrar", jsonRequest("""
                {"nome":"%s","email":"%s","senha":"%s"}
                """.formatted(nome, email, senha)), JsonNode.class);
    }

    private ResponseEntity<JsonNode> login(String email, String senha) {
        return restTemplate.postForEntity("/api/auth/login", jsonRequest("""
                {"email":"%s","senha":"%s"}
                """.formatted(email, senha)), JsonNode.class);
    }

    private HttpEntity<String> jsonRequest(String corpo) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(corpo, headers);
    }
}
