import { useCallback, useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import Layout from '../../components/Layout.jsx';
import Loading from '../../components/Loading.jsx';
import ErrorMessage from '../../components/ErrorMessage.jsx';
import StatusBadge from '../../components/StatusBadge.jsx';
import arbitrationService from '../../api/arbitrationService.js';
import { filenameFromDisposition, saveBlob } from '../../utils/download.js';
import {
  formatDateTime,
  labelEstadoArbitraje,
  parseApiError,
  variantEstadoArbitraje,
} from '../../utils/format.js';

export default function AdminArbitrationDetailPage() {
  const { id } = useParams();
  const [arbitration, setArbitration] = useState(null);
  const [decision, setDecision] = useState('A_FAVOR_PROFESIONAL');
  const [observacion, setObservacion] = useState('');
  const [loading, setLoading] = useState(true);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  const load = useCallback(() => {
    setLoading(true);
    setError(null);
    arbitrationService.getForAdmin(id)
      .then(setArbitration)
      .catch((err) => setError(parseApiError(err)))
      .finally(() => setLoading(false));
  }, [id]);

  useEffect(() => { load(); }, [load]);

  async function handleResolve(e) {
    e.preventDefault();
    setError(null);
    setSuccess(null);
    if (observacion.trim().length < 5) {
      setError('La observación debe tener al menos 5 caracteres.');
      return;
    }
    setBusy(true);
    try {
      const updated = await arbitrationService.resolve(id, { decision, observacion: observacion.trim() });
      setArbitration(updated);
      setSuccess('Arbitraje resuelto correctamente.');
    } catch (err) {
      setError(parseApiError(err));
    } finally {
      setBusy(false);
    }
  }

  async function download(type) {
    setError(null);
    try {
      const response = type === 'cliente'
        ? await arbitrationService.downloadClientFile(id)
        : await arbitrationService.downloadProfessionalFile(id);
      const fallback = type === 'cliente' ? arbitration.archivoClienteNombre : arbitration.archivoProfesionalNombre;
      saveBlob(response.data, filenameFromDisposition(response.headers?.['content-disposition'], fallback || 'soporte'));
    } catch (err) {
      setError(parseApiError(err));
    }
  }

  const isFinal = arbitration && ['RESUELTO_A_FAVOR_CLIENTE', 'RESUELTO_A_FAVOR_PROFESIONAL', 'CERRADO'].includes(arbitration.estado);

  return (
    <Layout>
      <section className="page">
        <header className="page-header">
          <div className="page-header-row">
            <div>
              <h2>Detalle de arbitraje</h2>
              <p className="page-subtitle">Revisa la información y toma una decisión administrativa.</p>
            </div>
            <Link to="/admin/arbitrations" className="btn btn-ghost">← Arbitrajes</Link>
          </div>
        </header>

        {loading && <Loading text="Cargando caso…" />}
        <ErrorMessage message={error} onRetry={load} />
        {success && <div className="alert alert-success">✓ {success}</div>}

        {!loading && arbitration && (
          <>
            <article className="detail-card">
              <div className="page-header-row">
                <div>
                  <h3>{arbitration.proyectoTitulo}</h3>
                  <p className="muted">Cliente: {arbitration.clienteNombre} · Profesional: {arbitration.profesionalNombre}</p>
                </div>
                <StatusBadge label={labelEstadoArbitraje(arbitration.estado)} variant={variantEstadoArbitraje(arbitration.estado)} />
              </div>
              <dl className="quote-card-meta">
                <div><dt>Fecha creación</dt><dd>{formatDateTime(arbitration.fechaCreacion)}</dd></div>
                <div><dt>Fecha respuesta</dt><dd>{formatDateTime(arbitration.fechaRespuestaProfesional)}</dd></div>
                <div><dt>Fecha resolución</dt><dd>{formatDateTime(arbitration.fechaResolucion)}</dd></div>
                <div><dt>Pago</dt><dd>#{arbitration.pagoId}</dd></div>
              </dl>
              <p><strong>Motivo cliente:</strong> {arbitration.motivoCliente}</p>
              {arbitration.respuestaProfesional && <p><strong>Respuesta profesional:</strong> {arbitration.respuestaProfesional}</p>}
              {arbitration.decisionAdmin && <p><strong>Decisión admin:</strong> {arbitration.decisionAdmin}</p>}
              <div className="quote-card-actions">
                {arbitration.archivoClienteNombre && <button className="btn btn-ghost" onClick={() => download('cliente')}>Descargar soporte cliente</button>}
                {arbitration.archivoProfesionalNombre && <button className="btn btn-ghost" onClick={() => download('profesional')}>Descargar soporte profesional</button>}
              </div>
            </article>

            {!isFinal && (
              <form className="form-card" onSubmit={handleResolve}>
                <h3>Resolver arbitraje</h3>
                <label className="form-field">
                  <span>Decisión</span>
                  <select value={decision} onChange={(e) => setDecision(e.target.value)}>
                    <option value="A_FAVOR_PROFESIONAL">Resolver a favor del profesional y liberar pago</option>
                    <option value="A_FAVOR_CLIENTE">Resolver a favor del cliente y devolver pago</option>
                    <option value="SOLICITAR_CORRECCION">Solicitar corrección y mantener pago retenido</option>
                  </select>
                </label>
                <label className="form-field">
                  <span>Observación administrativa</span>
                  <textarea rows={5} value={observacion} onChange={(e) => setObservacion(e.target.value)} />
                </label>
                <button className="btn btn-primary" disabled={busy}>{busy ? 'Resolviendo…' : 'Resolver arbitraje'}</button>
              </form>
            )}
          </>
        )}
      </section>
    </Layout>
  );
}
