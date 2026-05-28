package com.freelancehub.controllers.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class RegisterProfessionalRequest {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no es válido")
    @Size(max = 150)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 100, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150)
    private String nombre;

    @Size(max = 30)
    private String telefono;

    private String biografia;

    @PositiveOrZero(message = "La tarifa promedio no puede ser negativa")
    private BigDecimal tarifaPromedio;

    private String certificaciones;

    @Size(max = 200)
    private String areaExperiencia;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getBiografia() { return biografia; }
    public void setBiografia(String biografia) { this.biografia = biografia; }

    public BigDecimal getTarifaPromedio() { return tarifaPromedio; }
    public void setTarifaPromedio(BigDecimal tarifaPromedio) { this.tarifaPromedio = tarifaPromedio; }

    public String getCertificaciones() { return certificaciones; }
    public void setCertificaciones(String certificaciones) { this.certificaciones = certificaciones; }

    public String getAreaExperiencia() { return areaExperiencia; }
    public void setAreaExperiencia(String areaExperiencia) { this.areaExperiencia = areaExperiencia; }
}
