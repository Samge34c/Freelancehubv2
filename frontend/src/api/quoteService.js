import api from './api.js';

/**
 * Wrapper sobre los endpoints de cotizaciones del backend.
 * Las cotizaciones viven bajo dos rutas:
 *   - /projects/{projectId}/quotes    → crear, listar por proyecto, aceptar
 *   - /quotes/my                       → cotizaciones del profesional autenticado
 */
const quoteService = {
  /**
   * PROFESIONAL: crear cotización para un proyecto ABIERTO.
   * @param {number} projectId
   * @param {{precio:number, plazo:number, descripcion:string}} payload
   */
  create(projectId, payload) {
    return api.post(`/projects/${projectId}/quotes`, payload).then((r) => r.data);
  },

  /**
   * CLIENTE dueño: listar cotizaciones recibidas para un proyecto.
   * @returns QuoteResponse[]
   */
  listByProject(projectId) {
    return api.get(`/projects/${projectId}/quotes`).then((r) => r.data);
  },

  /**
   * PROFESIONAL: listar mis cotizaciones.
   * @returns QuoteResponse[]
   */
  listMine() {
    return api.get('/quotes/my').then((r) => r.data);
  },

  /**
   * CLIENTE dueño: aceptar una cotización PENDIENTE de su proyecto.
   * Tras aceptar, el proyecto pasa a EN_CONTRATO y las demás
   * cotizaciones PENDIENTES quedan RECHAZADAS.
   * @param {number} projectId
   * @param {number} quoteId
   */
  accept(projectId, quoteId) {
    return api.put(`/projects/${projectId}/quotes/${quoteId}/accept`).then((r) => r.data);
  },
};

export default quoteService;
