package com.freelancehub.controllers;

import com.freelancehub.controllers.dtos.auth.AuthResponse;
import com.freelancehub.controllers.dtos.auth.LoginRequest;
import com.freelancehub.controllers.dtos.auth.RegisterClientRequest;
import com.freelancehub.controllers.dtos.auth.RegisterProfessionalRequest;
import com.freelancehub.controllers.dtos.responses.UserResponse;
import com.freelancehub.security.SecurityUtils;
import com.freelancehub.services.AuthService;
import com.freelancehub.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Endpoints de registro, login y usuario autenticado")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/register/client")
    @Operation(summary = "Registrar nuevo cliente (público)")
    public ResponseEntity<AuthResponse> registerClient(
            @Valid @RequestBody RegisterClientRequest request) {
        AuthResponse response = authService.registerClient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/register/professional")
    @Operation(summary = "Registrar nuevo profesional (público)")
    public ResponseEntity<AuthResponse> registerProfessional(
            @Valid @RequestBody RegisterProfessionalRequest request) {
        AuthResponse response = authService.registerProfessional(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión y obtener JWT (público)")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Datos del usuario autenticado a partir del JWT")
    public ResponseEntity<UserResponse> me() {
        Long currentId = SecurityUtils.getCurrentUserIdOrThrow();
        return ResponseEntity.ok(userService.getById(currentId));
    }
}
