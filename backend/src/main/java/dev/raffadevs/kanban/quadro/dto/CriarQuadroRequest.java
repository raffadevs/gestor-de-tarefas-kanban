package dev.raffadevs.kanban.quadro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CriarQuadroRequest(
        @NotBlank(message = "O nome do quadro é obrigatório")
        @Size(max = 120)
        String nome
) {
}
