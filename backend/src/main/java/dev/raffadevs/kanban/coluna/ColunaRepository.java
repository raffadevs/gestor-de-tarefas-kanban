package dev.raffadevs.kanban.coluna;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ColunaRepository extends JpaRepository<Coluna, Long> {

    List<Coluna> findByQuadroIdOrderByPosicaoAsc(Long quadroId);

    @Query("SELECT MAX(c.posicao) FROM Coluna c WHERE c.quadroId = :quadroId")
    Optional<BigDecimal> buscarMaiorPosicao(@Param("quadroId") Long quadroId);
}
