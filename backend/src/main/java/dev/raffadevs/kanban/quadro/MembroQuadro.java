package dev.raffadevs.kanban.quadro;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "membro_quadro")
public class MembroQuadro {

    @EmbeddedId
    private MembroQuadroId id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Papel papel;

    @Column(name = "entrou_em", nullable = false)
    private Instant entrouEm;

    protected MembroQuadro() {
    }

    public MembroQuadro(Long quadroId, Long usuarioId, Papel papel) {
        this.id = new MembroQuadroId(quadroId, usuarioId);
        this.papel = papel;
        this.entrouEm = Instant.now();
    }

    public MembroQuadroId getId() {
        return id;
    }

    public Papel getPapel() {
        return papel;
    }

    public void setPapel(Papel papel) {
        this.papel = papel;
    }

    public Instant getEntrouEm() {
        return entrouEm;
    }
}
