package com.freelancehub.repositories;

import com.freelancehub.entities.Proyecto;
import com.freelancehub.enums.EstadoProyecto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {

    /**
     * Variante con EntityGraph para evitar LazyInitializationException
     * y N+1 queries en el mapper. Hibernate trae cliente y categoria
     * en la misma consulta (LEFT JOIN FETCH bajo el capó).
     */
    @EntityGraph(attributePaths = {"cliente", "categoria"})
    List<Proyecto> findByEstado(EstadoProyecto estado);

    @EntityGraph(attributePaths = {"cliente", "categoria"})
    List<Proyecto> findByClienteId(Long clienteId);

    /**
     * findById con EntityGraph para que el detalle de un proyecto
     * pueda ser serializado sin sesión abierta. Convive con el
     * findById heredado de JpaRepository (los métodos del service
     * pueden seguir usando ese si no necesitan las relaciones).
     */
    @EntityGraph(attributePaths = {"cliente", "categoria"})
    Optional<Proyecto> findWithRelationsById(Long id);

    @EntityGraph(attributePaths = {"cliente", "categoria"})
    @Query("SELECT p FROM Proyecto p " +
            "WHERE p.estado = :estado " +
            "AND (:categoriaId IS NULL OR p.categoria.id = :categoriaId) " +
            "AND (:minBudget IS NULL OR p.presupuesto >= :minBudget) " +
            "AND (:maxBudget IS NULL OR p.presupuesto <= :maxBudget) " +
            "ORDER BY p.fechaCreacion DESC")
    List<Proyecto> findOpenProjectsWithFilters(
            @Param("estado") EstadoProyecto estado,
            @Param("categoriaId") Long categoriaId,
            @Param("minBudget") BigDecimal minBudget,
            @Param("maxBudget") BigDecimal maxBudget
    );
}
