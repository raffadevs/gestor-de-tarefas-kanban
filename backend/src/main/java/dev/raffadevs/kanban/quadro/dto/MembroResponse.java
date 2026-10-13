package dev.raffadevs.kanban.quadro.dto;

import dev.raffadevs.kanban.quadro.Papel;

public record MembroResponse(Long usuarioId, String nome, String email, Papel papel) {
}
