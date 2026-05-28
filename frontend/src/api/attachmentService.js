import api from './api.js';

/** Adjuntos opcionales del proyecto: requisitos, briefs o documentos de referencia. */
const attachmentService = {
  upload(projectId, file) {
    const formData = new FormData();
    formData.append('file', file);

    return api
      .post(`/projects/${projectId}/attachments`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      })
      .then((r) => r.data);
  },

  listByProject(projectId) {
    return api.get(`/projects/${projectId}/attachments`).then((r) => r.data);
  },

  download(attachmentId) {
    return api.get(`/projects/attachments/${attachmentId}/download`, {
      responseType: 'blob',
    });
  },
};

export default attachmentService;
