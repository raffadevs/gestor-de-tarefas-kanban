package dev.raffadevs.kanban.security;

/**
 * Principal leve extraído diretamente das claims do JWT — evita uma consulta
 * ao banco a cada requisição autenticada (o ganho de desempenho de um token
 * stateless).
 */
public record AuthenticatedUser(Long id, String email, String nome) {
}
