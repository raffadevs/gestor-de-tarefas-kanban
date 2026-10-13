package dev.raffadevs.kanban;

import org.springframework.boot.test.context.SpringBootTest;
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
}
