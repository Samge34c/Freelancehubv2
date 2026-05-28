package com.freelancehub.controllers.dtos.responses;

import com.freelancehub.enums.EstadoEvidencia;

import java.time.LocalDateTime;

public class EvidenciaResponse {

    private Long id;
    private Long proyectoId;
    private String proyectoTitulo;
    private Long profesionalId;
    private String profesionalNombre;
    private String nombreArchivo;
    private String tipoArchivo;
    private String descripcion;
    private LocalDateTime fechaSubida;
    private EstadoEvidencia estado;
    private String urlDescarga;

    public EvidenciaResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProyectoId() { return proyectoId; }
    public void setProyectoId(Long proyectoId) { this.proyectoId = proyectoId; }

    public String getProyectoTitulo() { return proyectoTitulo; }
    public void setProyectoTitulo(String proyectoTitulo) { this.proyectoTitulo = proyectoTitulo; }

    public Long getProfesionalId() { return profesionalId; }
    public void setProfesionalId(Long profesionalId) { this.profesionalId = profesionalId; }

    public String getProfesionalNombre() { return profesionalNombre; }
    public void setProfesionalNombre(String profesionalNombre) { this.profesionalNombre = profesionalNombre; }

    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }

    public String getTipoArchivo() { return tipoArchivo; }
    public void setTipoArchivo(String tipoArchivo) { this.tipoArchivo = tipoArchivo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDateTime getFechaSubida() { return fechaSubida; }
    public void setFechaSubida(LocalDateTime fechaSubida) { this.fechaSubida = fechaSubida; }

    public EstadoEvidencia getEstado() { return estado; }
    public void setEstado(EstadoEvidencia estado) { this.estado = estado; }

    public String getUrlDescarga() { return urlDescarga; }
    public void setUrlDescarga(String urlDescarga) { this.urlDescarga = urlDescarga; }
}
