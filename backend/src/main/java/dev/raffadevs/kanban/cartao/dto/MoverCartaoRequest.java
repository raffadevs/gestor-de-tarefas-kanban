package dev.raffadevs.kanban.cartao.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MoverCartaoRequest(
        @NotNull(message = "A coluna de destino é obrigatória") Long colunaDestinoId,

        @NotNull(message = "O índice de destino é obrigatório")
        @Min(value = 0, message = "O índice de destino não pode ser negativo")
        Integer indiceDestino
) {
}
