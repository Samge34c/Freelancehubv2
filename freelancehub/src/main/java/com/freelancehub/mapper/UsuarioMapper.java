package com.freelancehub.mapper;

import com.freelancehub.controllers.dtos.responses.UserResponse;
import com.freelancehub.entities.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UserResponse toResponse(Usuario usuario);
}
