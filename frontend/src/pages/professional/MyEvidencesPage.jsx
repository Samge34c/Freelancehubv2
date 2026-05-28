import { useCallback, useEffect, useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import Layout from '../../components/Layout.jsx';
import Loading from '../../components/Loading.jsx';
import ErrorMessage from '../../components/ErrorMessage.jsx';
import EmptyState from '../../components/EmptyState.jsx';
import EvidenceCard from '../../components/EvidenceCard.jsx';
import evidenceService from '../../api/evidenceService.js';
import { parseApiError } from '../../utils/format.js';
import { filenameFromDisposition, saveBlob } from '../../utils/download.js';

export default function MyEvidencesPage() {
  const location = useLocation();
  const justUploaded = location.state?.uploaded === true;

  const [evidences, setEvidences] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [downloadError, setDownloadError] = useState(null);
  const [busyId, setBusyId] = useState(null);

  const load = useCallback(() => {
    setLoading(true);
    setError(null);
    evidenceService
      .listMine()
      .then((list) => setEvidences(Array.isArray(list) ? list : []))
      .catch((err) => setError(parseApiError(err)))
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  async function handleDownload(evidence) {
    setBusyId(evidence.id);
    setDownloadError(null);
    try {
      const response = await evidenceService.download(evidence.id);
      const filename = filenameFromDisposition(
        response.headers?.['content-disposition'],
        evidence.nombreArchivo || 'evidencia'
      );
      saveBlob(response.data, filename);
    } catch (err) {
      setDownloadError(parseApiError(err));
    } finally {
      setBusyId(null);
    }
  }

  return (
    <Layout>
      <section className="page">
        <header className="page-header">
          <div className="page-header-row">
            <div>
              <h2>Mis entregas</h2>
              <p className="page-subtitle">
                Evidencias y documentos que has enviado a tus clientes.
              </p>
            </div>
            <Link to="/professional/quotes" className="btn btn-primary">
              Buscar cotizaciones aceptadas
            </Link>
          </div>
        </header>

        {justUploaded && (
          <div className="alert alert-success" role="status">
            ✓ Evidencia subida correctamente.
          </div>
        )}

        {loading && <Loading text="Cargando evidencias…" />}
        <ErrorMessage message={error} onRetry={load} />
        <ErrorMessage message={downloadError} />

        {!loading && !error && evidences.length === 0 && (
          <EmptyState
            title="Aún no has subido evidencias"
            description="Cuando tengas una cotización aceptada, podrás subir evidencia desde Mis cotizaciones."
            action={
              <Link to="/professional/quotes" className="btn btn-primary">
                Ir a mis cotizaciones
              </Link>
            }
          />
        )}

        {!loading && !error && evidences.length > 0 && (
          <div className="evidence-list">
            {evidences.map((e) => (
              <EvidenceCard
                key={e.id}
                evidence={e}
                perspective="profesional"
                onDownload={handleDownload}
                busy={busyId === e.id}
              />
            ))}
          </div>
        )}
      </section>
    </Layout>
  );
}
