package com.freelancehub.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "profesional_perfiles")
public class ProfesionalPerfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Column(columnDefinition = "TEXT")
    private String biografia;

    @Column(name = "tarifa_promedio", precision = 14, scale = 2)
    private BigDecimal tarifaPromedio;

    @Column(columnDefinition = "TEXT")
    private String certificaciones;

    @Column(name = "area_experiencia", length = 200)
    private String areaExperiencia;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getBiografia() { return biografia; }
    public void setBiografia(String biografia) { this.biografia = biografia; }

    public BigDecimal getTarifaPromedio() { return tarifaPromedio; }
    public void setTarifaPromedio(BigDecimal tarifaPromedio) { this.tarifaPromedio = tarifaPromedio; }

    public String getCertificaciones() { return certificaciones; }
    public void setCertificaciones(String certificaciones) { this.certificaciones = certificaciones; }

    public String getAreaExperiencia() { return areaExperiencia; }
    public void setAreaExperiencia(String areaExperiencia) { this.areaExperiencia = areaExperiencia; }
}
