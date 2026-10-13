package dev.raffadevs.kanban.quadro;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuadroRepository extends JpaRepository<Quadro, Long> {

    @Query("""
            SELECT q FROM Quadro q
            JOIN MembroQuadro m ON m.id.quadroId = q.id
            WHERE m.id.usuarioId = :usuarioId
            ORDER BY q.criadoEm DESC
            """)
    List<Quadro> buscarQuadrosDoUsuario(@Param("usuarioId") Long usuarioId);
}
