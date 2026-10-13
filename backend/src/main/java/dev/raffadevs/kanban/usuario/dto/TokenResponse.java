package dev.raffadevs.kanban.usuario.dto;

public record TokenResponse(String accessToken, String refreshToken, UsuarioResponse usuario) {
}
