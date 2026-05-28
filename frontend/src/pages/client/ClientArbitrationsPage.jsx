import { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import Layout from '../../components/Layout.jsx';
import Loading from '../../components/Loading.jsx';
import ErrorMessage from '../../components/ErrorMessage.jsx';
import EmptyState from '../../components/EmptyState.jsx';
import ArbitrationCard from '../../components/ArbitrationCard.jsx';
import arbitrationService from '../../api/arbitrationService.js';
import { filenameFromDisposition, saveBlob } from '../../utils/download.js';
import { parseApiError } from '../../utils/format.js';

export default function ClientArbitrationsPage() {
  const location = useLocation();
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [actionError, setActionError] = useState(null);

  function load() {
    setLoading(true);
    setError(null);
    arbitrationService.listMineAsClient()
      .then((data) => setItems(Array.isArray(data) ? data : []))
      .catch((err) => setError(parseApiError(err)))
      .finally(() => setLoading(false));
  }

  useEffect(() => { load(); }, []);

  async function download(type, arbitration) {
    setActionError(null);
    try {
      const response = type === 'cliente'
        ? await arbitrationService.downloadClientFile(arbitration.id)
        : await arbitrationService.downloadProfessionalFile(arbitration.id);
      const fallback = type === 'cliente' ? arbitration.archivoClienteNombre : arbitration.archivoProfesionalNombre;
      saveBlob(response.data, filenameFromDisposition(response.headers?.['content-disposition'], fallback || 'soporte'));
    } catch (err) {
      setActionError(parseApiError(err));
    }
  }

  return (
    <Layout>
      <section className="page">
        <header className="page-header">
          <h2>Mis arbitrajes</h2>
          <p className="page-subtitle">Revisa los reclamos enviados para revisión administrativa.</p>
        </header>
        {location.state?.created && <div className="alert alert-success">✓ Arbitraje creado correctamente.</div>}
        {loading && <Loading text="Cargando arbitrajes…" />}
        <ErrorMessage message={error} onRetry={load} />
        <ErrorMessage message={actionError} />
        {!loading && !error && items.length === 0 && <EmptyState title="No tienes arbitrajes" description="Cuando solicites revisión sobre una evidencia aparecerá aquí." />}
        {!loading && !error && items.length > 0 && (
          <div className="quote-list">
            {items.map((a) => (
              <ArbitrationCard
                key={a.id}
                arbitration={a}
                perspective="cliente"
                extraActions={(
                  <>
                    {a.archivoClienteNombre && <button className="btn btn-ghost" onClick={() => download('cliente', a)}>Descargar soporte cliente</button>}
                    {a.archivoProfesionalNombre && <button className="btn btn-ghost" onClick={() => download('profesional', a)}>Descargar respuesta profesional</button>}
                  </>
                )}
              />
            ))}
          </div>
        )}
      </section>
    </Layout>
  );
}
