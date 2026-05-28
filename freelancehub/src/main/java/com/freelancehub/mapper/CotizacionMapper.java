package com.freelancehub.mapper;

import com.freelancehub.controllers.dtos.responses.QuoteResponse;
import com.freelancehub.entities.Cotizacion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CotizacionMapper {

    @Mapping(target = "proyectoId", source = "proyecto.id")
    @Mapping(target = "proyectoTitulo", source = "proyecto.titulo")
    @Mapping(target = "profesionalId", source = "profesional.id")
    @Mapping(target = "profesionalNombre", source = "profesional.nombre")
    QuoteResponse toResponse(Cotizacion cotizacion);
}
