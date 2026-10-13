package dev.raffadevs.kanban.etiqueta;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EtiquetaRepository extends JpaRepository<Etiqueta, Long> {

    List<Etiqueta> findByQuadroId(Long quadroId);
}
