package dev.raffadevs.kanban.usuario.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(
        @NotBlank(message = "O refreshToken é obrigatório") String refreshToken
) {
}
