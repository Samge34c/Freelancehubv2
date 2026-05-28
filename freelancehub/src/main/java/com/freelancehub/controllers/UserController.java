package com.freelancehub.controllers;

import com.freelancehub.controllers.dtos.request.UpdateUserRequest;
import com.freelancehub.controllers.dtos.responses.UserResponse;
import com.freelancehub.security.SecurityUtils;
import com.freelancehub.security.UserPrincipal;
import com.freelancehub.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Usuarios", description = "Operaciones sobre el usuario autenticado")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('CLIENTE','PROFESIONAL','ADMIN')")
    @Operation(summary = "Datos del usuario autenticado")
    public ResponseEntity<UserResponse> getMe() {
        UserPrincipal principal = SecurityUtils.getCurrentUserOrThrow();
        return ResponseEntity.ok(userService.getById(principal.getId()));
    }

    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('CLIENTE','PROFESIONAL')")
    @Operation(summary = "Actualizar datos básicos del usuario autenticado")
    public ResponseEntity<UserResponse> updateMe(@Valid @RequestBody UpdateUserRequest request) {
        Long currentId = SecurityUtils.getCurrentUserIdOrThrow();
        return ResponseEntity.ok(userService.updateOwn(currentId, request));
    }
}
