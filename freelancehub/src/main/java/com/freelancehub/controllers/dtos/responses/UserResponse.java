package com.freelancehub.controllers.dtos.responses;

import com.freelancehub.enums.EstadoUsuario;
import com.freelancehub.enums.RolUsuario;

import java.time.LocalDateTime;

public class UserResponse {

    private Long id;
    private String email;
    private String nombre;
    private String telefono;
    private RolUsuario rol;
    private EstadoUsuario estado;
    private LocalDateTime fechaRegistro;

    public UserResponse() {}

    public UserResponse(Long id, String email, String nombre, String telefono,
                        RolUsuario rol, EstadoUsuario estado, LocalDateTime fechaRegistro) {
        this.id = id;
        this.email = email;
        this.nombre = nombre;
        this.telefono = telefono;
        this.rol = rol;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public RolUsuario getRol() { return rol; }
    public void setRol(RolUsuario rol) { this.rol = rol; }

    public EstadoUsuario getEstado() { return estado; }
    public void setEstado(EstadoUsuario estado) { this.estado = estado; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}
