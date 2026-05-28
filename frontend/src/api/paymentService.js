import api from './api.js';

/**
 * Endpoints de pagos simulados tipo escrow.
 * No hay dinero real: es un flujo académico de estados.
 */
const paymentService = {
  /** CLIENTE: crear pago simulado retenido para un proyecto contratado. */
  createForProject(projectId) {
    return api.post(`/projects/${projectId}/payments/simulate`).then((r) => r.data);
  },

  /** CLIENTE/PROFESIONAL/ADMIN: consultar pago de un proyecto. */
  getForProject(projectId) {
    return api.get(`/projects/${projectId}/payments`).then((r) => r.data);
  },

  /** CLIENTE: liberar pago simulado después de aprobar evidencia. */
  release(paymentId) {
    return api.put(`/payments/${paymentId}/release`).then((r) => r.data);
  },

  /** CLIENTE o PROFESIONAL: listar pagos relacionados con el usuario autenticado. */
  listMine() {
    return api.get('/payments/my').then((r) => r.data);
  },
};

export default paymentService;
