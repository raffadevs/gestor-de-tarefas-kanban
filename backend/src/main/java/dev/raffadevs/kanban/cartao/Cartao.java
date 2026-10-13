package dev.raffadevs.kanban.cartao;

import dev.raffadevs.kanban.etiqueta.Etiqueta;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cartao")
public class Cartao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coluna_id", nullable = false)
    private Long colunaId;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "responsavel_id")
    private Long responsavelId;

    private Instant prazo;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal posicao;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @ManyToMany
    @JoinTable(
            name = "cartao_etiqueta",
            joinColumns = @JoinColumn(name = "cartao_id"),
            inverseJoinColumns = @JoinColumn(name = "etiqueta_id")
    )
    private Set<Etiqueta> etiquetas = new HashSet<>();

    protected Cartao() {
    }

    public Cartao(Long colunaId, String titulo, String descricao, Long responsavelId, Instant prazo,
                  BigDecimal posicao) {
        this.colunaId = colunaId;
        this.titulo = titulo;
        this.descricao = descricao;
        this.responsavelId = responsavelId;
        this.prazo = prazo;
        this.posicao = posicao;
        this.criadoEm = Instant.now();
    }

    public void mover(Long novaColunaId, BigDecimal novaPosicao) {
        this.colunaId = novaColunaId;
        this.posicao = novaPosicao;
    }

    public void atualizar(String titulo, String descricao, Long responsavelId, Instant prazo) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.responsavelId = responsavelId;
        this.prazo = prazo;
    }

    public Long getId() {
        return id;
    }

    public Long getColunaId() {
        return colunaId;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public Long getResponsavelId() {
        return responsavelId;
    }

    public Instant getPrazo() {
        return prazo;
    }

    public BigDecimal getPosicao() {
        return posicao;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Set<Etiqueta> getEtiquetas() {
        return etiquetas;
    }
}
