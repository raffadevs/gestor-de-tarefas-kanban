package dev.raffadevs.kanban.coluna.dto;

import dev.raffadevs.kanban.coluna.Coluna;

import java.math.BigDecimal;

public record ColunaResponse(Long id, Long quadroId, String nome, BigDecimal posicao) {

    public static ColunaResponse from(Coluna coluna) {
        return new ColunaResponse(coluna.getId(), coluna.getQuadroId(), coluna.getNome(), coluna.getPosicao());
    }
}
