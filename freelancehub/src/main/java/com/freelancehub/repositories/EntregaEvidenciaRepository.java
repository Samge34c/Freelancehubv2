package com.freelancehub.repositories;

import com.freelancehub.entities.EntregaEvidencia;
import com.freelancehub.enums.EstadoEvidencia;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EntregaEvidenciaRepository extends JpaRepository<EntregaEvidencia, Long> {

    @EntityGraph(attributePaths = {"proyecto", "proyecto.cliente", "profesional"})
    List<EntregaEvidencia> findByProyectoIdOrderByFechaSubidaDesc(Long proyectoId);

    @EntityGraph(attributePaths = {"proyecto", "proyecto.cliente", "profesional"})
    List<EntregaEvidencia> findByProfesionalIdOrderByFechaSubidaDesc(Long profesionalId);

    @EntityGraph(attributePaths = {"proyecto", "proyecto.cliente", "profesional"})
    Optional<EntregaEvidencia> findWithRelationsById(Long id);

    boolean existsByProyectoIdAndEstado(Long proyectoId, EstadoEvidencia estado);
}
