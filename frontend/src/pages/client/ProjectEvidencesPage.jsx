import { useCallback, useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import Layout from '../../components/Layout.jsx';
import Loading from '../../components/Loading.jsx';
import ErrorMessage from '../../components/ErrorMessage.jsx';
import EmptyState from '../../components/EmptyState.jsx';
import EvidenceCard from '../../components/EvidenceCard.jsx';
import StatusBadge from '../../components/StatusBadge.jsx';
import projectService from '../../api/projectService.js';
import evidenceService from '../../api/evidenceService.js';
import {
  formatCurrency,
  labelEstadoProyecto,
  parseApiError,
  variantEstadoProyecto,
} from '../../utils/format.js';
import { filenameFromDisposition, saveBlob } from '../../utils/download.js';

export default function ProjectEvidencesPage() {
  const { projectId } = useParams();

  const [project, setProject] = useState(null);
  const [evidences, setEvidences] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [actionError, setActionError] = useState(null);
  const [actionSuccess, setActionSuccess] = useState(null);
  const [busyId, setBusyId] = useState(null);

  const load = useCallback(() => {
    setLoading(true);
    setError(null);
    setActionError(null);
    Promise.all([
      projectService.getById(projectId),
      evidenceService.listByProject(projectId),
    ])
      .then(([proj, list]) => {
        setProject(proj);
        setEvidences(Array.isArray(list) ? list : []);
      })
      .catch((err) => setError(parseApiError(err)))
      .finally(() => setLoading(false));
  }, [projectId]);

  useEffect(() => {
    load();
  }, [load]);

  async function handleDownload(evidence) {
    setBusyId(evidence.id);
    setActionError(null);
    try {
      const response = await evidenceService.download(evidence.id);
      const filename = filenameFromDisposition(
        response.headers?.['content-disposition'],
        evidence.nombreArchivo || 'evidencia'
      );
      saveBlob(response.data, filename);
    } catch (err) {
      setActionError(parseApiError(err));
    } finally {
      setBusyId(null);
    }
  }

  async function handleApprove(evidence) {
    setBusyId(evidence.id);
    setActionError(null);
    setActionSuccess(null);
    try {
      await evidenceService.approve(evidence.id);
      setActionSuccess('Evidencia aprobada correctamente.');
      await load();
    } catch (err) {
      setActionError(parseApiError(err));
    } finally {
      setBusyId(null);
    }
  }

  async function handleReject(evidence) {
    setBusyId(evidence.id);
    setActionError(null);
    setActionSuccess(null);
    try {
      await evidenceService.reject(evidence.id);
      setActionSuccess('Evidencia rechazada correctamente.');
      await load();
    } catch (err) {
      setActionError(parseApiError(err));
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
              <h2>Evidencias del proyecto</h2>
              <p className="page-subtitle">
                Revisa, descarga y aprueba las evidencias enviadas por el profesional.
              </p>
            </div>
            <Link to="/client/projects" className="btn btn-ghost">
              ← Mis proyectos
            </Link>
          </div>
        </header>

        {loading && <Loading text="Cargando evidencias…" />}
        <ErrorMessage message={error} onRetry={load} />

        {!loading && !error && project && (
          <div className="project-summary">
            <div className="project-summary-info">
              <h3>{project.titulo}</h3>
              <p className="muted">
                Presupuesto: {formatCurrency(project.presupuesto)} · Categoría:{' '}
                {project.categoriaNombre || '—'}
              </p>
            </div>
            <StatusBadge
              label={labelEstadoProyecto(project.estado)}
              variant={variantEstadoProyecto(project.estado)}
            />
          </div>
        )}

        {actionSuccess && (
          <div className="alert alert-success" role="status">
            ✓ {actionSuccess}
          </div>
        )}
        <ErrorMessage message={actionError} />

        {!loading && !error && evidences.length === 0 && (
          <EmptyState
            title="Aún no hay evidencias"
            description="Cuando el profesional suba documentos de entrega aparecerán aquí."
          />
        )}

        {!loading && !error && evidences.length > 0 && (
          <div className="evidence-list">
            {evidences.map((e) => (
              <div key={e.id} className="evidence-wrapper">
                <EvidenceCard
                  evidence={e}
                  perspective="cliente"
                  onDownload={handleDownload}
                  onApprove={handleApprove}
                  onReject={handleReject}
                  busy={busyId === e.id}
                />
                {e.estado !== 'APROBADA' && project?.estado !== 'CERRADO' && (
                  <div className="card-inline-actions">
                    <Link
                      to={`/client/evidences/${e.id}/arbitration/new`}
                      className="btn btn-ghost"
                    >
                      Solicitar arbitraje
                    </Link>
                    <span className="muted small-text">
                      Usa esta opción si la entrega no cumple con lo acordado.
                    </span>
                  </div>
                )}
              </div>
            ))}
          </div>
        )}
      </section>
    </Layout>
  );
}
