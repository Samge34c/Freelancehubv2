package com.freelancehub.controllers.dtos.request;

import jakarta.validation.constraints.NotBlank;

public class ResolverArbitrajeRequest {

    @NotBlank(message = "La decisión es obligatoria")
    private String decision;

    @NotBlank(message = "La observación del administrador es obligatoria")
    private String observacion;

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}
