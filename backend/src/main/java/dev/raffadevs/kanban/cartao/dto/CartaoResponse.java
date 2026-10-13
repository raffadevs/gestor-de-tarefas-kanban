package dev.raffadevs.kanban.cartao.dto;

import dev.raffadevs.kanban.cartao.Cartao;
import dev.raffadevs.kanban.etiqueta.dto.EtiquetaResponse;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record CartaoResponse(
        Long id,
        Long colunaId,
        String titulo,
        String descricao,
        Long responsavelId,
        Instant prazo,
        BigDecimal posicao,
        Instant criadoEm,
        List<EtiquetaResponse> etiquetas
) {
    public static CartaoResponse from(Cartao cartao) {
        List<EtiquetaResponse> etiquetas = cartao.getEtiquetas().stream()
                .map(EtiquetaResponse::from)
                .toList();
        return new CartaoResponse(
                cartao.getId(), cartao.getColunaId(), cartao.getTitulo(), cartao.getDescricao(),
                cartao.getResponsavelId(), cartao.getPrazo(), cartao.getPosicao(), cartao.getCriadoEm(), etiquetas
        );
    }
}
