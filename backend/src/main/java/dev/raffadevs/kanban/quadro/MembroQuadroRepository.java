package dev.raffadevs.kanban.quadro;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MembroQuadroRepository extends JpaRepository<MembroQuadro, MembroQuadroId> {

    Optional<MembroQuadro> findById_QuadroIdAndId_UsuarioId(Long quadroId, Long usuarioId);

    List<MembroQuadro> findById_QuadroId(Long quadroId);

    boolean existsById_QuadroIdAndId_UsuarioId(Long quadroId, Long usuarioId);
}
