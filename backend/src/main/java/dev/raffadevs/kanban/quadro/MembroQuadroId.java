package dev.raffadevs.kanban.quadro;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class MembroQuadroId implements Serializable {

    private Long quadroId;
    private Long usuarioId;

    protected MembroQuadroId() {
    }

    public MembroQuadroId(Long quadroId, Long usuarioId) {
        this.quadroId = quadroId;
        this.usuarioId = usuarioId;
    }

    public Long getQuadroId() {
        return quadroId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MembroQuadroId that)) return false;
        return Objects.equals(quadroId, that.quadroId) && Objects.equals(usuarioId, that.usuarioId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quadroId, usuarioId);
    }
}
