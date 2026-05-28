package com.freelancehub.mapper;

import com.freelancehub.controllers.dtos.responses.ProjectResponse;
import com.freelancehub.entities.Proyecto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProyectoMapper {

    @Mapping(target = "clienteId", source = "cliente.id")
    @Mapping(target = "clienteNombre", source = "cliente.nombre")
    @Mapping(target = "categoriaId", source = "categoria.id")
    @Mapping(target = "categoriaNombre", source = "categoria.nombre")
    ProjectResponse toResponse(Proyecto proyecto);
}
