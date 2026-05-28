package com.freelancehub.controllers.dtos.responses;

import com.freelancehub.enums.EstadoArbitraje;

import java.time.LocalDateTime;

public class ArbitrajeResponse {

    private Long id;
    private Long proyectoId;
    private String proyectoTitulo;
    private Long evidenciaId;
    private Long pagoId;
    private Long clienteId;
    private String clienteNombre;
    private Long profesionalId;
    private String profesionalNombre;
    private String motivoCliente;
    private String archivoClienteNombre;
    private String urlDescargaArchivoCliente;
    private String respuestaProfesional;
    private String archivoProfesionalNombre;
    private String urlDescargaArchivoProfesional;
    private String decisionAdmin;
    private EstadoArbitraje estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaRespuestaProfesional;
    private LocalDateTime fechaResolucion;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProyectoId() { return proyectoId; }
    public void setProyectoId(Long proyectoId) { this.proyectoId = proyectoId; }

    public String getProyectoTitulo() { return proyectoTitulo; }
    public void setProyectoTitulo(String proyectoTitulo) { this.proyectoTitulo = proyectoTitulo; }

    public Long getEvidenciaId() { return evidenciaId; }
    public void setEvidenciaId(Long evidenciaId) { this.evidenciaId = evidenciaId; }

    public Long getPagoId() { return pagoId; }
    public void setPagoId(Long pagoId) { this.pagoId = pagoId; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public Long getProfesionalId() { return profesionalId; }
    public void setProfesionalId(Long profesionalId) { this.profesionalId = profesionalId; }

    public String getProfesionalNombre() { return profesionalNombre; }
    public void setProfesionalNombre(String profesionalNombre) { this.profesionalNombre = profesionalNombre; }

    public String getMotivoCliente() { return motivoCliente; }
    public void setMotivoCliente(String motivoCliente) { this.motivoCliente = motivoCliente; }

    public String getArchivoClienteNombre() { return archivoClienteNombre; }
    public void setArchivoClienteNombre(String archivoClienteNombre) { this.archivoClienteNombre = archivoClienteNombre; }

    public String getUrlDescargaArchivoCliente() { return urlDescargaArchivoCliente; }
    public void setUrlDescargaArchivoCliente(String urlDescargaArchivoCliente) { this.urlDescargaArchivoCliente = urlDescargaArchivoCliente; }

    public String getRespuestaProfesional() { return respuestaProfesional; }
    public void setRespuestaProfesional(String respuestaProfesional) { this.respuestaProfesional = respuestaProfesional; }

    public String getArchivoProfesionalNombre() { return archivoProfesionalNombre; }
    public void setArchivoProfesionalNombre(String archivoProfesionalNombre) { this.archivoProfesionalNombre = archivoProfesionalNombre; }

    public String getUrlDescargaArchivoProfesional() { return urlDescargaArchivoProfesional; }
    public void setUrlDescargaArchivoProfesional(String urlDescargaArchivoProfesional) { this.urlDescargaArchivoProfesional = urlDescargaArchivoProfesional; }

    public String getDecisionAdmin() { return decisionAdmin; }
    public void setDecisionAdmin(String decisionAdmin) { this.decisionAdmin = decisionAdmin; }

    public EstadoArbitraje getEstado() { return estado; }
    public void setEstado(EstadoArbitraje estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaRespuestaProfesional() { return fechaRespuestaProfesional; }
    public void setFechaRespuestaProfesional(LocalDateTime fechaRespuestaProfesional) { this.fechaRespuestaProfesional = fechaRespuestaProfesional; }

    public LocalDateTime getFechaResolucion() { return fechaResolucion; }
    public void setFechaResolucion(LocalDateTime fechaResolucion) { this.fechaResolucion = fechaResolucion; }
}
