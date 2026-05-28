import api from './api.js';

const arbitrationService = {
  create(evidenceId, payload) {
    const formData = new FormData();
    formData.append('motivo', payload.motivo);
    if (payload.file) formData.append('file', payload.file);
    return api.post(`/evidences/${evidenceId}/arbitrations`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    }).then((r) => r.data);
  },

  listMineAsClient() {
    return api.get('/arbitrations/my').then((r) => r.data);
  },

  listMineAsProfessional() {
    return api.get('/arbitrations/professional/my').then((r) => r.data);
  },

  respond(arbitrationId, payload) {
    const formData = new FormData();
    formData.append('respuesta', payload.respuesta);
    if (payload.file) formData.append('file', payload.file);
    return api.post(`/arbitrations/${arbitrationId}/response`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    }).then((r) => r.data);
  },

  listAllForAdmin() {
    return api.get('/admin/arbitrations').then((r) => r.data);
  },

  getForAdmin(id) {
    return api.get(`/admin/arbitrations/${id}`).then((r) => r.data);
  },

  resolve(id, payload) {
    return api.put(`/admin/arbitrations/${id}/resolve`, payload).then((r) => r.data);
  },

  downloadClientFile(id) {
    return api.get(`/arbitrations/${id}/client-file/download`, { responseType: 'blob' });
  },

  downloadProfessionalFile(id) {
    return api.get(`/arbitrations/${id}/professional-file/download`, { responseType: 'blob' });
  },
};

export default arbitrationService;
