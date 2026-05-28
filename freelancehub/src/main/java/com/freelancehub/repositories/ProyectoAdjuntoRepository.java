package com.freelancehub.repositories;

import com.freelancehub.entities.ProyectoAdjunto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProyectoAdjuntoRepository extends JpaRepository<ProyectoAdjunto, Long> {

    @EntityGraph(attributePaths = {"proyecto", "proyecto.cliente", "proyecto.categoria", "cliente"})
    List<ProyectoAdjunto> findByProyectoIdOrderByFechaSubidaDesc(Long proyectoId);

    @EntityGraph(attributePaths = {"proyecto", "proyecto.cliente", "proyecto.categoria", "cliente"})
    Optional<ProyectoAdjunto> findWithRelationsById(Long id);
}
