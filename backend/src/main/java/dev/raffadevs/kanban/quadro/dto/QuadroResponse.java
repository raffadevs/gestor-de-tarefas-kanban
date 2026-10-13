package dev.raffadevs.kanban.quadro.dto;

import dev.raffadevs.kanban.quadro.Papel;
import dev.raffadevs.kanban.quadro.Quadro;

import java.time.Instant;

public record QuadroResponse(Long id, String nome, Long donoId, Instant criadoEm, Papel meuPapel) {

    public static QuadroResponse from(Quadro quadro, Papel meuPapel) {
        return new QuadroResponse(quadro.getId(), quadro.getNome(), quadro.getDonoId(), quadro.getCriadoEm(), meuPapel);
    }
}
