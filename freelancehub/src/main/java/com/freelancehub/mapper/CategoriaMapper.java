package com.freelancehub.mapper;

import com.freelancehub.controllers.dtos.request.CategoryRequest;
import com.freelancehub.controllers.dtos.responses.CategoryResponse;
import com.freelancehub.entities.Categoria;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    CategoryResponse toResponse(Categoria categoria);

    Categoria toEntity(CategoryRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromRequest(CategoryRequest request, @MappingTarget Categoria categoria);
}
