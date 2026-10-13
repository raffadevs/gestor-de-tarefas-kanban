package dev.raffadevs.kanban.etiqueta;

import jakarta.persistence.*;

@Entity
@Table(name = "etiqueta")
public class Etiqueta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quadro_id", nullable = false)
    private Long quadroId;

    @Column(nullable = false, length = 40)
    private String nome;

    @Column(nullable = false, length = 7)
    private String cor;

    protected Etiqueta() {
    }

    public Etiqueta(Long quadroId, String nome, String cor) {
        this.quadroId = quadroId;
        this.nome = nome;
        this.cor = cor;
    }

    public Long getId() {
        return id;
    }

    public Long getQuadroId() {
        return quadroId;
    }

    public String getNome() {
        return nome;
    }

    public String getCor() {
        return cor;
    }
}
