package dev.raffadevs.kanban.cartao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CartaoRepository extends JpaRepository<Cartao, Long> {

    List<Cartao> findByColunaIdOrderByPosicaoAsc(Long colunaId);

    @Query("SELECT c FROM Cartao c WHERE c.colunaId IN :colunaIds ORDER BY c.posicao ASC")
    List<Cartao> buscarPorColunas(@Param("colunaIds") List<Long> colunaIds);

    @Query("SELECT MAX(c.posicao) FROM Cartao c WHERE c.colunaId = :colunaId")
    Optional<BigDecimal> buscarMaiorPosicao(@Param("colunaId") Long colunaId);
}
