package dev.raffadevs.kanban.coluna;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "coluna")
public class Coluna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quadro_id", nullable = false)
    private Long quadroId;

    @Column(nullable = false, length = 80)
    private String nome;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal posicao;

    protected Coluna() {
    }

    public Coluna(Long quadroId, String nome, BigDecimal posicao) {
        this.quadroId = quadroId;
        this.nome = nome;
        this.posicao = posicao;
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

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getPosicao() {
        return posicao;
    }
}
