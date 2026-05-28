package com.freelancehub.repositories;

import com.freelancehub.entities.PagoSimulado;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PagoSimuladoRepository extends JpaRepository<PagoSimulado, Long> {

    @EntityGraph(attributePaths = {
            "proyecto", "proyecto.cliente", "proyecto.categoria",
            "cotizacion", "cliente", "profesional"
    })
    Optional<PagoSimulado> findByProyectoId(Long proyectoId);

    @EntityGraph(attributePaths = {
            "proyecto", "proyecto.cliente", "proyecto.categoria",
            "cotizacion", "cliente", "profesional"
    })
    Optional<PagoSimulado> findWithRelationsById(Long id);

    @EntityGraph(attributePaths = {
            "proyecto", "proyecto.cliente", "proyecto.categoria",
            "cotizacion", "cliente", "profesional"
    })
    List<PagoSimulado> findByClienteIdOrderByFechaCreacionDesc(Long clienteId);

    @EntityGraph(attributePaths = {
            "proyecto", "proyecto.cliente", "proyecto.categoria",
            "cotizacion", "cliente", "profesional"
    })
    List<PagoSimulado> findByProfesionalIdOrderByFechaCreacionDesc(Long profesionalId);

    @Override
    @EntityGraph(attributePaths = {
            "proyecto", "proyecto.cliente", "proyecto.categoria",
            "cotizacion", "cliente", "profesional"
    })
    List<PagoSimulado> findAll();
}
