package com.freelancehub.repositories;

import com.freelancehub.entities.Habilidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HabilidadRepository extends JpaRepository<Habilidad, Long> {

    List<Habilidad> findByActivoTrue();

    Optional<Habilidad> findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}
