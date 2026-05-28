/**
 * Utilidades compartidas para presentación.
 */

// ------------------------------------------------------------------
// Estados
// ------------------------------------------------------------------

const ESTADO_PROYECTO_LABELS = {
  ABIERTO: 'Abierto',
  COTIZADO: 'Cotizado',
  EN_CONTRATO: 'En contrato',
  EN_PROCESO: 'En proceso',
  EN_REVISION: 'En revisión',
  CERRADO: 'Cerrado',
  CANCELADO: 'Cancelado',
  EN_DISPUTA: 'En disputa',
};

const ESTADO_COTIZACION_LABELS = {
  PENDIENTE: 'Pendiente',
  ACEPTADA: 'Aceptada',
  RECHAZADA: 'Rechazada',
  CANCELADA: 'Cancelada',
};

const ESTADO_EVIDENCIA_LABELS = {
  ENVIADA: 'Enviada',
  APROBADA: 'Aprobada',
  RECHAZADA: 'Rechazada',
};

const ESTADO_PAGO_LABELS = {
  RETENIDO: 'Retenido',
  LIBERADO: 'Liberado',
  DEVUELTO: 'Devuelto al cliente',
  CANCELADO: 'Cancelado',
};

const ESTADO_ARBITRAJE_LABELS = {
  ABIERTO: 'Abierto',
  RESPONDIDO: 'Respondido',
  EN_REVISION: 'En revisión',
  RESUELTO_A_FAVOR_CLIENTE: 'Resuelto a favor del cliente',
  RESUELTO_A_FAVOR_PROFESIONAL: 'Resuelto a favor del profesional',
  CORRECCION_SOLICITADA: 'Corrección solicitada',
  CERRADO: 'Cerrado',
};

export function labelEstadoProyecto(estado) {
  return ESTADO_PROYECTO_LABELS[estado] || estado || '—';
}

export function labelEstadoCotizacion(estado) {
  return ESTADO_COTIZACION_LABELS[estado] || estado || '—';
}

export function labelEstadoEvidencia(estado) {
  return ESTADO_EVIDENCIA_LABELS[estado] || estado || '—';
}

export function labelEstadoPago(estado) {
  return ESTADO_PAGO_LABELS[estado] || estado || '—';
}

export function labelEstadoArbitraje(estado) {
  return ESTADO_ARBITRAJE_LABELS[estado] || estado || '—';
}

/**
 * Devuelve la "variante" CSS para un badge según el estado.
 * Se usa así: <span className={`badge badge-${variantEstadoProyecto(estado)}`}>
 */
export function variantEstadoProyecto(estado) {
  switch (estado) {
    case 'ABIERTO':
      return 'open';
    case 'COTIZADO':
    case 'EN_CONTRATO':
    case 'EN_PROCESO':
    case 'EN_REVISION':
      return 'active';
    case 'CERRADO':
      return 'done';
    case 'CANCELADO':
    case 'EN_DISPUTA':
      return 'warn';
    default:
      return 'neutral';
  }
}

export function variantEstadoCotizacion(estado) {
  switch (estado) {
    case 'PENDIENTE':
      return 'pending';
    case 'ACEPTADA':
      return 'success';
    case 'RECHAZADA':
    case 'CANCELADA':
      return 'warn';
    default:
      return 'neutral';
  }
}

export function variantEstadoEvidencia(estado) {
  switch (estado) {
    case 'ENVIADA':
      return 'pending';
    case 'APROBADA':
      return 'success';
    case 'RECHAZADA':
      return 'warn';
    default:
      return 'neutral';
  }
}

export function variantEstadoPago(estado) {
  switch (estado) {
    case 'RETENIDO':
      return 'pending';
    case 'LIBERADO':
      return 'success';
    case 'DEVUELTO':
    case 'CANCELADO':
      return 'warn';
    default:
      return 'neutral';
  }
}

export function variantEstadoArbitraje(estado) {
  switch (estado) {
    case 'ABIERTO':
    case 'RESPONDIDO':
    case 'EN_REVISION':
    case 'CORRECCION_SOLICITADA':
      return 'pending';
    case 'RESUELTO_A_FAVOR_PROFESIONAL':
      return 'success';
    case 'RESUELTO_A_FAVOR_CLIENTE':
      return 'warn';
    case 'CERRADO':
      return 'done';
    default:
      return 'neutral';
  }
}

// ------------------------------------------------------------------
// Moneda y fechas
// ------------------------------------------------------------------

const cop = new Intl.NumberFormat('es-CO', {
  style: 'currency',
  currency: 'COP',
  maximumFractionDigits: 0,
});

export function formatCurrency(value) {
  if (value == null || value === '') return '—';
  const n = typeof value === 'number' ? value : Number(value);
  if (Number.isNaN(n)) return '—';
  return cop.format(n);
}

export function formatDate(iso) {
  if (!iso) return '—';
  try {
    const d = new Date(iso);
    if (Number.isNaN(d.getTime())) return '—';
    return d.toLocaleDateString('es-CO', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  } catch {
    return '—';
  }
}

export function formatDateTime(iso) {
  if (!iso) return '—';
  try {
    const d = new Date(iso);
    if (Number.isNaN(d.getTime())) return '—';
    return d.toLocaleString('es-CO', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  } catch {
    return '—';
  }
}

export function formatDays(n) {
  if (n == null) return '—';
  if (n === 1) return '1 día';
  return `${n} días`;
}

// ------------------------------------------------------------------
// Parseo de errores Axios → mensaje legible
// ------------------------------------------------------------------

/**
 * Toma un error de Axios y devuelve un string en español apto
 * para mostrar al usuario. Aplica los códigos solicitados:
 *  - 401 → "Sesión expirada..."
 *  - 403 → "No tienes permisos para esta acción"
 *  - 404 → "No se encontró el recurso solicitado"
 *  - 4xx → mensaje del backend si viene, si no, genérico
 *  - 5xx → "El servidor reportó un error..."
 *  - sin response → "No se pudo conectar con el servidor"
 *
 * El backend devuelve ErrorResponse con campo "mensaje" (en español).
 * También probamos "message", "error" y "details" como fallback.
 */
export function parseApiError(err) {
  if (!err) return 'Ocurrió un error inesperado.';

  const status = err.response?.status;
  const data = err.response?.data;
  const backendMessage = data?.mensaje || data?.message || data?.error;

  if (!err.response) {
    return 'No se pudo conectar con el servidor. ¿Está corriendo el backend en :8080?';
  }
  if (status === 401) {
    return 'Tu sesión expiró. Inicia sesión de nuevo.';
  }
  if (status === 403) {
    return 'No tienes permisos para esta acción.';
  }
  if (status === 404) {
    return 'No se encontró el recurso solicitado.';
  }
  if (status === 400 && Array.isArray(data?.details) && data.details.length > 0) {
    // Errores de validación: mostrar el primero para no abrumar
    return data.details[0];
  }
  if (status >= 400 && status < 500) {
    return backendMessage || 'No fue posible completar la solicitud.';
  }
  if (status >= 500) {
    return 'El servidor reportó un error. Intenta de nuevo.';
  }
  return backendMessage || 'Ocurrió un error inesperado.';
}
