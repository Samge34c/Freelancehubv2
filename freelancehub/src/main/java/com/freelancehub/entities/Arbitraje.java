package com.freelancehub.entities;

import com.freelancehub.enums.EstadoArbitraje;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "arbitrajes")
public class Arbitraje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evidencia_id", nullable = false)
    private EntregaEvidencia evidencia;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pago_simulado_id", nullable = false)
    private PagoSimulado pagoSimulado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profesional_id", nullable = false)
    private Usuario profesional;

    @Column(name = "motivo_cliente", nullable = false, columnDefinition = "TEXT")
    private String motivoCliente;

    @Column(name = "archivo_cliente_nombre", length = 255)
    private String archivoClienteNombre;

    @Column(name = "archivo_cliente_almacenado", length = 255)
    private String archivoClienteAlmacenado;

    @Column(name = "archivo_cliente_tipo", length = 120)
    private String archivoClienteTipo;

    @Column(name = "archivo_cliente_ruta", length = 600)
    private String archivoClienteRuta;

    @Column(name = "respuesta_profesional", columnDefinition = "TEXT")
    private String respuestaProfesional;

    @Column(name = "archivo_profesional_nombre", length = 255)
    private String archivoProfesionalNombre;

    @Column(name = "archivo_profesional_almacenado", length = 255)
    private String archivoProfesionalAlmacenado;

    @Column(name = "archivo_profesional_tipo", length = 120)
    private String archivoProfesionalTipo;

    @Column(name = "archivo_profesional_ruta", length = 600)
    private String archivoProfesionalRuta;

    @Column(name = "decision_admin", columnDefinition = "TEXT")
    private String decisionAdmin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private EstadoArbitraje estado;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_respuesta_profesional")
    private LocalDateTime fechaRespuestaProfesional;

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoArbitraje.ABIERTO;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Proyecto getProyecto() { return proyecto; }
    public void setProyecto(Proyecto proyecto) { this.proyecto = proyecto; }

    public EntregaEvidencia getEvidencia() { return evidencia; }
    public void setEvidencia(EntregaEvidencia evidencia) { this.evidencia = evidencia; }

    public PagoSimulado getPagoSimulado() { return pagoSimulado; }
    public void setPagoSimulado(PagoSimulado pagoSimulado) { this.pagoSimulado = pagoSimulado; }

    public Usuario getCliente() { return cliente; }
    public void setCliente(Usuario cliente) { this.cliente = cliente; }

    public Usuario getProfesional() { return profesional; }
    public void setProfesional(Usuario profesional) { this.profesional = profesional; }

    public String getMotivoCliente() { return motivoCliente; }
    public void setMotivoCliente(String motivoCliente) { this.motivoCliente = motivoCliente; }

    public String getArchivoClienteNombre() { return archivoClienteNombre; }
    public void setArchivoClienteNombre(String archivoClienteNombre) { this.archivoClienteNombre = archivoClienteNombre; }

    public String getArchivoClienteAlmacenado() { return archivoClienteAlmacenado; }
    public void setArchivoClienteAlmacenado(String archivoClienteAlmacenado) { this.archivoClienteAlmacenado = archivoClienteAlmacenado; }

    public String getArchivoClienteTipo() { return archivoClienteTipo; }
    public void setArchivoClienteTipo(String archivoClienteTipo) { this.archivoClienteTipo = archivoClienteTipo; }

    public String getArchivoClienteRuta() { return archivoClienteRuta; }
    public void setArchivoClienteRuta(String archivoClienteRuta) { this.archivoClienteRuta = archivoClienteRuta; }

    public String getRespuestaProfesional() { return respuestaProfesional; }
    public void setRespuestaProfesional(String respuestaProfesional) { this.respuestaProfesional = respuestaProfesional; }

    public String getArchivoProfesionalNombre() { return archivoProfesionalNombre; }
    public void setArchivoProfesionalNombre(String archivoProfesionalNombre) { this.archivoProfesionalNombre = archivoProfesionalNombre; }

    public String getArchivoProfesionalAlmacenado() { return archivoProfesionalAlmacenado; }
    public void setArchivoProfesionalAlmacenado(String archivoProfesionalAlmacenado) { this.archivoProfesionalAlmacenado = archivoProfesionalAlmacenado; }

    public String getArchivoProfesionalTipo() { return archivoProfesionalTipo; }
    public void setArchivoProfesionalTipo(String archivoProfesionalTipo) { this.archivoProfesionalTipo = archivoProfesionalTipo; }

    public String getArchivoProfesionalRuta() { return archivoProfesionalRuta; }
    public void setArchivoProfesionalRuta(String archivoProfesionalRuta) { this.archivoProfesionalRuta = archivoProfesionalRuta; }

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
