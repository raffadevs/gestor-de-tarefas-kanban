package dev.raffadevs.kanban.coluna.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AtualizarColunaRequest(
        @NotBlank(message = "O nome da coluna é obrigatório")
        @Size(max = 80)
        String nome
) {
}
