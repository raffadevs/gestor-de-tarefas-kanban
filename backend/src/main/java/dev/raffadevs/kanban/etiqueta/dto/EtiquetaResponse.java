package dev.raffadevs.kanban.etiqueta.dto;

import dev.raffadevs.kanban.etiqueta.Etiqueta;

public record EtiquetaResponse(Long id, Long quadroId, String nome, String cor) {

    public static EtiquetaResponse from(Etiqueta etiqueta) {
        return new EtiquetaResponse(etiqueta.getId(), etiqueta.getQuadroId(), etiqueta.getNome(), etiqueta.getCor());
    }
}
