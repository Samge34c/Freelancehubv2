package com.freelancehub.repositories;

import com.freelancehub.entities.Arbitraje;
import com.freelancehub.enums.EstadoArbitraje;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ArbitrajeRepository extends JpaRepository<Arbitraje, Long> {

    @EntityGraph(attributePaths = {
            "proyecto", "proyecto.cliente", "proyecto.categoria",
            "evidencia", "pagoSimulado", "cliente", "profesional"
    })
    Optional<Arbitraje> findWithRelationsById(Long id);

    @EntityGraph(attributePaths = {
            "proyecto", "proyecto.cliente", "proyecto.categoria",
            "evidencia", "pagoSimulado", "cliente", "profesional"
    })
    List<Arbitraje> findByClienteIdOrderByFechaCreacionDesc(Long clienteId);

    @EntityGraph(attributePaths = {
            "proyecto", "proyecto.cliente", "proyecto.categoria",
            "evidencia", "pagoSimulado", "cliente", "profesional"
    })
    List<Arbitraje> findByProfesionalIdOrderByFechaCreacionDesc(Long profesionalId);

    @Override
    @EntityGraph(attributePaths = {
            "proyecto", "proyecto.cliente", "proyecto.categoria",
            "evidencia", "pagoSimulado", "cliente", "profesional"
    })
    List<Arbitraje> findAll();

    boolean existsByEvidenciaIdAndEstadoIn(Long evidenciaId, Collection<EstadoArbitraje> estados);

    boolean existsByProyectoIdAndEstadoIn(Long proyectoId, Collection<EstadoArbitraje> estados);
}
