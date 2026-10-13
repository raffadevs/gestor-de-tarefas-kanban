package dev.raffadevs.kanban.cartao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record CriarCartaoRequest(
        @NotNull(message = "A coluna é obrigatória") Long colunaId,

        @NotBlank(message = "O título é obrigatório")
        @Size(max = 200)
        String titulo,

        String descricao,
        Long responsavelId,
        Instant prazo
) {
}
