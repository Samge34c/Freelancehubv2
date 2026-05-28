import api from './api.js';

/**
 * Endpoints de evidencias/documentos de entrega.
 */
const evidenceService = {
  /**
   * PROFESIONAL: subir evidencia para un proyecto contratado.
   * @param {number|string} projectId
   * @param {{file: File, descripcion: string}} payload
   */
  upload(projectId, payload) {
    const formData = new FormData();
    formData.append('file', payload.file);
    formData.append('descripcion', payload.descripcion);

    return api
      .post(`/projects/${projectId}/evidences`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      })
      .then((r) => r.data);
  },

  /** CLIENTE: listar evidencias de un proyecto propio. */
  listByProject(projectId) {
    return api.get(`/projects/${projectId}/evidences`).then((r) => r.data);
  },

  /** PROFESIONAL: listar mis evidencias subidas. */
  listMine() {
    return api.get('/evidences/my').then((r) => r.data);
  },

  /** CLIENTE dueño: aprobar evidencia. */
  approve(evidenceId) {
    return api.put(`/evidences/${evidenceId}/approve`).then((r) => r.data);
  },

  /** CLIENTE dueño: rechazar evidencia. */
  reject(evidenceId) {
    return api.put(`/evidences/${evidenceId}/reject`).then((r) => r.data);
  },

  /** Descarga segura: usa Authorization porque el endpoint está protegido. */
  download(evidenceId) {
    return api.get(`/evidences/${evidenceId}/download`, {
      responseType: 'blob',
    });
  },
};

export default evidenceService;
