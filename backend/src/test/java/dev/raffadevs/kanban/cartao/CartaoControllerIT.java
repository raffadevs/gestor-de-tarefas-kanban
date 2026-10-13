package dev.raffadevs.kanban.cartao;

import com.fasterxml.jackson.databind.JsonNode;
import dev.raffadevs.kanban.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CartaoControllerIT extends IntegrationTestBase {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void moverCartaoParaOInicioDaColunaGravaApenasAPosicaoDele() {
        String token = registrarEObterToken("dono4@exemplo.com", "senha12345", "Dono");
        Long quadroId = criarQuadro(token, "Quadro de teste").get("id").asLong();
        Long colunaId = criarColuna(token, quadroId, "A fazer").get("id").asLong();

        JsonNode card1 = criarCartao(token, quadroId, colunaId, "Primeira tarefa");
        JsonNode card2 = criarCartao(token, quadroId, colunaId, "Segunda tarefa");
        BigDecimal posicaoOriginalCard1 = new BigDecimal(card1.get("posicao").asText());

        ResponseEntity<JsonNode> resposta = restTemplate.exchange(
                "/api/quadros/" + quadroId + "/cartoes/" + card2.get("id").asLong() + "/mover",
                HttpMethod.PATCH,
                autenticado(token, """
                        {"colunaDestinoId":%d,"indiceDestino":0}
                        """.formatted(colunaId)),
                JsonNode.class);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        BigDecimal novaPosicaoCard2 = new BigDecimal(resposta.getBody().get("posicao").asText());
        assertThat(novaPosicaoCard2).isLessThan(posicaoOriginalCard1);
    }

    @Test
    void filtroPorPrazoVencidoRetornaApenasCartoesAtrasados() {
        String token = registrarEObterToken("dono5@exemplo.com", "senha12345", "Dono");
        Long quadroId = criarQuadro(token, "Quadro com prazos").get("id").asLong();
        Long colunaId = criarColuna(token, quadroId, "A fazer").get("id").asLong();

        restTemplate.exchange("/api/quadros/" + quadroId + "/cartoes", HttpMethod.POST,
                autenticado(token, """
                        {"colunaId":%d,"titulo":"Vencido","prazo":"2020-01-01T00:00:00Z"}
                        """.formatted(colunaId)), JsonNode.class);
        restTemplate.exchange("/api/quadros/" + quadroId + "/cartoes", HttpMethod.POST,
                autenticado(token, """
                        {"colunaId":%d,"titulo":"No prazo","prazo":"2099-01-01T00:00:00Z"}
                        """.formatted(colunaId)), JsonNode.class);

        ResponseEntity<JsonNode> resposta = restTemplate.exchange(
                "/api/quadros/" + quadroId + "/cartoes?prazoVencido=true", HttpMethod.GET,
                autenticado(token, null), JsonNode.class);

        assertThat(resposta.getBody()).hasSize(1);
        assertThat(resposta.getBody().get(0).get("titulo").asText()).isEqualTo("Vencido");
    }

    private String registrarEObterToken(String email, String senha, String nome) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>("""
                {"nome":"%s","email":"%s","senha":"%s"}
                """.formatted(nome, email, senha), headers);
        return restTemplate.postForEntity("/api/auth/registrar", request, JsonNode.class)
                .getBody().get("accessToken").asText();
    }

    private JsonNode criarQuadro(String token, String nome) {
        return restTemplate.exchange("/api/quadros", HttpMethod.POST,
                autenticado(token, """
                        {"nome":"%s"}
                        """.formatted(nome)), JsonNode.class).getBody();
    }

    private JsonNode criarColuna(String token, Long quadroId, String nome) {
        return restTemplate.exchange("/api/quadros/" + quadroId + "/colunas", HttpMethod.POST,
                autenticado(token, """
                        {"nome":"%s"}
                        """.formatted(nome)), JsonNode.class).getBody();
    }

    private JsonNode criarCartao(String token, Long quadroId, Long colunaId, String titulo) {
        return restTemplate.exchange("/api/quadros/" + quadroId + "/cartoes", HttpMethod.POST,
                autenticado(token, """
                        {"colunaId":%d,"titulo":"%s"}
                        """.formatted(colunaId, titulo)), JsonNode.class).getBody();
    }

    private HttpEntity<String> autenticado(String token, String corpo) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return new HttpEntity<>(corpo, headers);
    }
}
