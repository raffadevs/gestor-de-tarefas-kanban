package dev.raffadevs.kanban.quadro;

import com.fasterxml.jackson.databind.JsonNode;
import dev.raffadevs.kanban.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

class QuadroControllerIT extends IntegrationTestBase {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void donoCriaQuadroEConvidaMembroComPapelLeitor() {
        String tokenDono = registrarEObterToken("dono@exemplo.com", "senha12345", "Dona Maria");
        registrarEObterToken("convidado@exemplo.com", "senha12345", "Convidado");

        JsonNode quadro = criarQuadro(tokenDono, "Sprint 1").getBody();
        Long quadroId = quadro.get("id").asLong();
        assertThat(quadro.get("meuPapel").asText()).isEqualTo("DONO");

        ResponseEntity<JsonNode> convite = restTemplate.exchange(
                "/api/quadros/" + quadroId + "/membros", HttpMethod.POST,
                autenticado(tokenDono, """
                        {"email":"convidado@exemplo.com","papel":"LEITOR"}
                        """),
                JsonNode.class);

        assertThat(convite.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(convite.getBody().get("papel").asText()).isEqualTo("LEITOR");
    }

    @Test
    void leitorNaoConseguerCriarColuna() {
        String tokenDono = registrarEObterToken("dono2@exemplo.com", "senha12345", "Dono");
        String tokenLeitor = registrarEObterToken("leitor2@exemplo.com", "senha12345", "Leitor");

        Long quadroId = criarQuadro(tokenDono, "Quadro restrito").getBody().get("id").asLong();
        restTemplate.exchange("/api/quadros/" + quadroId + "/membros", HttpMethod.POST,
                autenticado(tokenDono, """
                        {"email":"leitor2@exemplo.com","papel":"LEITOR"}
                        """), JsonNode.class);

        ResponseEntity<JsonNode> resposta = restTemplate.exchange(
                "/api/quadros/" + quadroId + "/colunas", HttpMethod.POST,
                autenticado(tokenLeitor, """
                        {"nome":"A fazer"}
                        """), JsonNode.class);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void naoMembroNaoConsegueVerQuadro() {
        String tokenDono = registrarEObterToken("dono3@exemplo.com", "senha12345", "Dono");
        String tokenEstranho = registrarEObterToken("estranho3@exemplo.com", "senha12345", "Estranho");

        Long quadroId = criarQuadro(tokenDono, "Quadro privado").getBody().get("id").asLong();

        ResponseEntity<JsonNode> resposta = restTemplate.exchange(
                "/api/quadros/" + quadroId, HttpMethod.GET, autenticado(tokenEstranho, null), JsonNode.class);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private String registrarEObterToken(String email, String senha, String nome) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>("""
                {"nome":"%s","email":"%s","senha":"%s"}
                """.formatted(nome, email, senha), headers);
        ResponseEntity<JsonNode> resposta = restTemplate.postForEntity("/api/auth/registrar", request, JsonNode.class);
        return resposta.getBody().get("accessToken").asText();
    }

    private ResponseEntity<JsonNode> criarQuadro(String token, String nome) {
        return restTemplate.exchange("/api/quadros", HttpMethod.POST,
                autenticado(token, """
                        {"nome":"%s"}
                        """.formatted(nome)), JsonNode.class);
    }

    private HttpEntity<String> autenticado(String token, String corpo) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return new HttpEntity<>(corpo, headers);
    }
}
