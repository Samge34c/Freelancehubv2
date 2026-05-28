package com.freelancehub.services;

import com.freelancehub.controllers.dtos.request.HabilidadRequest;
import com.freelancehub.controllers.dtos.responses.HabilidadResponse;
import com.freelancehub.entities.Habilidad;
import com.freelancehub.exception.BusinessException;
import com.freelancehub.exception.ResourceNotFoundException;
import com.freelancehub.mapper.HabilidadMapper;
import com.freelancehub.repositories.HabilidadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HabilidadService {

    private final HabilidadRepository habilidadRepository;
    private final HabilidadMapper habilidadMapper;

    public HabilidadService(HabilidadRepository habilidadRepository,
                            HabilidadMapper habilidadMapper) {
        this.habilidadRepository = habilidadRepository;
        this.habilidadMapper = habilidadMapper;
    }

    public List<HabilidadResponse> listActive() {
        return habilidadRepository.findByActivoTrue().stream()
                .map(habilidadMapper::toResponse)
                .toList();
    }

    public List<HabilidadResponse> listAll() {
        return habilidadRepository.findAll().stream()
                .map(habilidadMapper::toResponse)
                .toList();
    }

    public HabilidadResponse getById(Long id) {
        Habilidad h = habilidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Habilidad no encontrada con id: " + id));
        return habilidadMapper.toResponse(h);
    }

    @Transactional
    public HabilidadResponse create(HabilidadRequest request) {
        if (habilidadRepository.existsByNombre(request.getNombre())) {
            throw new BusinessException(
                    "Ya existe una habilidad con el nombre: " + request.getNombre());
        }
        Habilidad h = habilidadMapper.toEntity(request);
        if (h.getActivo() == null) {
            h.setActivo(true);
        }
        h = habilidadRepository.save(h);
        return habilidadMapper.toResponse(h);
    }

    @Transactional
    public HabilidadResponse update(Long id, HabilidadRequest request) {
        Habilidad h = habilidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Habilidad no encontrada con id: " + id));

        if (request.getNombre() != null && !request.getNombre().equals(h.getNombre())) {
            habilidadRepository.findByNombre(request.getNombre())
                    .filter(existing -> !existing.getId().equals(id))
                    .ifPresent(existing -> {
                        throw new BusinessException(
                                "Ya existe otra habilidad con el nombre: " + request.getNombre());
                    });
        }

        habilidadMapper.updateFromRequest(request, h);
        h = habilidadRepository.save(h);
        return habilidadMapper.toResponse(h);
    }

    /** Soft-delete: marca la habilidad como inactiva sin borrarla físicamente. */
    @Transactional
    public void delete(Long id) {
        Habilidad h = habilidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Habilidad no encontrada con id: " + id));
        h.setActivo(false);
        habilidadRepository.save(h);
    }
}
