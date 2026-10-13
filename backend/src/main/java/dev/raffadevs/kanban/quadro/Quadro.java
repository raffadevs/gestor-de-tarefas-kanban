package dev.raffadevs.kanban.quadro;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "quadro")
public class Quadro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(name = "dono_id", nullable = false)
    private Long donoId;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    protected Quadro() {
    }

    public Quadro(String nome, Long donoId) {
        this.nome = nome;
        this.donoId = donoId;
        this.criadoEm = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Long getDonoId() {
        return donoId;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }
}
