package dev.raffadevs.kanban.cartao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record AtualizarCartaoRequest(
        @NotBlank(message = "O título é obrigatório")
        @Size(max = 200)
        String titulo,

        String descricao,
        Long responsavelId,
        Instant prazo
) {
}
