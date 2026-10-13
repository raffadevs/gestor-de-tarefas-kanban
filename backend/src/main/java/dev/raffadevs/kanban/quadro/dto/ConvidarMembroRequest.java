package dev.raffadevs.kanban.quadro.dto;

import dev.raffadevs.kanban.quadro.Papel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ConvidarMembroRequest(
        @NotBlank @Email(message = "E-mail inválido") String email,
        @NotNull(message = "O papel é obrigatório") Papel papel
) {
}
