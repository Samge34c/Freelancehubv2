import { useCallback, useEffect, useState } from 'react';
import attachmentService from '../api/attachmentService.js';
import ErrorMessage from './ErrorMessage.jsx';
import Loading from './Loading.jsx';
import { filenameFromDisposition, saveBlob } from '../utils/download.js';
import { formatDateTime, parseApiError } from '../utils/format.js';

export default function AttachmentList({ projectId, title = 'Archivos adjuntos del proyecto' }) {
  const [attachments, setAttachments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [downloadingId, setDownloadingId] = useState(null);

  const load = useCallback(() => {
    setLoading(true);
    setError(null);
    attachmentService
      .listByProject(projectId)
      .then((list) => setAttachments(Array.isArray(list) ? list : []))
      .catch((err) => setError(parseApiError(err)))
      .finally(() => setLoading(false));
  }, [projectId]);

  useEffect(() => {
    load();
  }, [load]);

  async function download(attachment) {
    setDownloadingId(attachment.id);
    setError(null);
    try {
      const response = await attachmentService.download(attachment.id);
      const filename = filenameFromDisposition(
        response.headers?.['content-disposition'],
        attachment.nombreArchivo || 'adjunto-proyecto'
      );
      saveBlob(response.data, filename);
    } catch (err) {
      setError(parseApiError(err));
    } finally {
      setDownloadingId(null);
    }
  }

  return (
    <section className="detail-card attachment-section">
      <header className="detail-head">
        <h3>{title}</h3>
      </header>

      <p className="page-subtitle">
        Documentos de requisitos, referencias o instrucciones que el cliente adjuntó al crear el proyecto.
      </p>

      {loading && <Loading text="Cargando adjuntos…" />}
      <ErrorMessage message={error} onRetry={load} />

      {!loading && !error && attachments.length === 0 && (
        <div className="empty-inline">Este proyecto no tiene archivos adjuntos.</div>
      )}

      {!loading && !error && attachments.length > 0 && (
        <div className="attachment-list">
          {attachments.map((attachment) => (
            <article className="attachment-item" key={attachment.id}>
              <div>
                <strong>{attachment.nombreArchivo}</strong>
                <span>{attachment.tipoArchivo || 'application/octet-stream'}</span>
                <small>Subido: {formatDateTime(attachment.fechaSubida)}</small>
              </div>
              <button
                type="button"
                className="btn btn-ghost"
                onClick={() => download(attachment)}
                disabled={downloadingId === attachment.id}
              >
                {downloadingId === attachment.id ? 'Descargando…' : 'Descargar'}
              </button>
            </article>
          ))}
        </div>
      )}
    </section>
  );
}
