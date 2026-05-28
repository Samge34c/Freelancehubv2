package com.freelancehub.controllers.dtos.responses;

import com.freelancehub.enums.EstadoPagoSimulado;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PagoSimuladoResponse {

    private Long id;
    private Long proyectoId;
    private String proyectoTitulo;
    private Long cotizacionId;
    private Long clienteId;
    private String clienteNombre;
    private Long profesionalId;
    private String profesionalNombre;
    private BigDecimal monto;
    private EstadoPagoSimulado estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaLiberacion;
    private String referencia;
    private String descripcion;
    private String notaSimulacion;

    public PagoSimuladoResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProyectoId() { return proyectoId; }
    public void setProyectoId(Long proyectoId) { this.proyectoId = proyectoId; }

    public String getProyectoTitulo() { return proyectoTitulo; }
    public void setProyectoTitulo(String proyectoTitulo) { this.proyectoTitulo = proyectoTitulo; }

    public Long getCotizacionId() { return cotizacionId; }
    public void setCotizacionId(Long cotizacionId) { this.cotizacionId = cotizacionId; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public Long getProfesionalId() { return profesionalId; }
    public void setProfesionalId(Long profesionalId) { this.profesionalId = profesionalId; }

    public String getProfesionalNombre() { return profesionalNombre; }
    public void setProfesionalNombre(String profesionalNombre) { this.profesionalNombre = profesionalNombre; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public EstadoPagoSimulado getEstado() { return estado; }
    public void setEstado(EstadoPagoSimulado estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaLiberacion() { return fechaLiberacion; }
    public void setFechaLiberacion(LocalDateTime fechaLiberacion) { this.fechaLiberacion = fechaLiberacion; }

    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getNotaSimulacion() { return notaSimulacion; }
    public void setNotaSimulacion(String notaSimulacion) { this.notaSimulacion = notaSimulacion; }
}
