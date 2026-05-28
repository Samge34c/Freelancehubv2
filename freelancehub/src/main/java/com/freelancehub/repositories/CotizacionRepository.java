package com.freelancehub.repositories;

import com.freelancehub.entities.Cotizacion;
import com.freelancehub.enums.EstadoCotizacion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CotizacionRepository extends JpaRepository<Cotizacion, Long> {

    @EntityGraph(attributePaths = {"proyecto", "proyecto.cliente", "proyecto.categoria", "profesional"})
    List<Cotizacion> findByProyectoId(Long proyectoId);

    @EntityGraph(attributePaths = {"proyecto", "proyecto.cliente", "proyecto.categoria", "profesional"})
    List<Cotizacion> findByProfesionalId(Long profesionalId);

    @EntityGraph(attributePaths = {"proyecto", "proyecto.cliente", "proyecto.categoria", "profesional"})
    Optional<Cotizacion> findByProyectoIdAndEstado(Long proyectoId, EstadoCotizacion estado);

    boolean existsByProyectoIdAndProfesionalId(Long proyectoId, Long profesionalId);

    boolean existsByProyectoIdAndProfesionalIdAndEstado(Long proyectoId, Long profesionalId, EstadoCotizacion estado);
}
