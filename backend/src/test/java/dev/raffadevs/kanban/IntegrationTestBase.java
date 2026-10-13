package dev.raffadevs.kanban;

import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Container compartilhado entre todas as subclasses de teste de integração.
 *
 * Deliberadamente NÃO usa @Testcontainers/@Container: como o campo estático
 * vive nesta superclasse, a extensão do JUnit encerraria o container ao
 * final da PRIMEIRA classe de teste que rodasse, quebrando as demais (bug já
 * visto no projeto encurtador-de-links). O padrão "singleton container"
 * abaixo inicia o container uma única vez por JVM e deixa o Ryuk (reaper do
 * Testcontainers) encerrá-lo ao final da execução.
 */
@Import(IntegrationTestBase.TestRestTemplateConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class IntegrationTestBase {

    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("kanban")
            .withUsername("kanban")
            .withPassword("kanban");

    static {
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void propriedades(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    /**
     * O cliente HTTP padrão do TestRestTemplate (baseado em HttpURLConnection
     * do JDK) não suporta o método PATCH e falha com "cannot retry due to
     * server authentication, in streaming mode" ao receber um 401 em uma
     * requisição POST com corpo. Trocamos para o Apache HttpClient 5, que não
     * tem nenhuma dessas limitações.
     */
    @TestConfiguration
    static class TestRestTemplateConfig {

        @Bean
        RestTemplateBuilder restTemplateBuilder() {
            return new RestTemplateBuilder()
                    .requestFactory(() -> new HttpComponentsClientHttpRequestFactory(HttpClients.createDefault()));
        }
    }
}
