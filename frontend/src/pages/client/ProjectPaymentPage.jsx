import { useCallback, useEffect, useMemo, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import Layout from '../../components/Layout.jsx';
import Loading from '../../components/Loading.jsx';
import ErrorMessage from '../../components/ErrorMessage.jsx';
import StatusBadge from '../../components/StatusBadge.jsx';
import PaymentCard from '../../components/PaymentCard.jsx';
import projectService from '../../api/projectService.js';
import paymentService from '../../api/paymentService.js';
import evidenceService from '../../api/evidenceService.js';
import {
  formatCurrency,
  labelEstadoProyecto,
  parseApiError,
  variantEstadoProyecto,
} from '../../utils/format.js';

function canCreatePayment(project) {
  return ['EN_CONTRATO', 'EN_PROCESO', 'EN_REVISION'].includes(project?.estado);
}

export default function ProjectPaymentPage() {
  const { projectId } = useParams();

  const [project, setProject] = useState(null);
  const [payment, setPayment] = useState(null);
  const [evidences, setEvidences] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [actionError, setActionError] = useState(null);
  const [actionSuccess, setActionSuccess] = useState(null);
  const [busy, setBusy] = useState(false);

  const hasApprovedEvidence = useMemo(
    () => evidences.some((e) => e.estado === 'APROBADA'),
    [evidences]
  );

  const load = useCallback(() => {
    setLoading(true);
    setError(null);
    setActionError(null);
    Promise.all([
      projectService.getById(projectId),
      paymentService.getForProject(projectId).catch((err) => {
        if (err.response?.status === 404) return null;
        throw err;
      }),
      evidenceService.listByProject(projectId).catch((err) => {
        if (err.response?.status === 404) return [];
        throw err;
      }),
    ])
      .then(([proj, pay, evs]) => {
        setProject(proj);
        setPayment(pay);
        setEvidences(Array.isArray(evs) ? evs : []);
      })
      .catch((err) => setError(parseApiError(err)))
      .finally(() => setLoading(false));
  }, [projectId]);

  useEffect(() => {
    load();
  }, [load]);

  async function handleCreate() {
    setBusy(true);
    setActionError(null);
    setActionSuccess(null);
    try {
      const created = await paymentService.createForProject(projectId);
      setPayment(created);
      setActionSuccess('Pago simulado creado correctamente. Quedó retenido tipo escrow.');
    } catch (err) {
      setActionError(parseApiError(err));
    } finally {
      setBusy(false);
    }
  }

  async function handleRelease() {
    if (!payment || !hasApprovedEvidence) return;
    setBusy(true);
    setActionError(null);
    setActionSuccess(null);
    try {
      const released = await paymentService.release(payment.id);
      setPayment(released);
      setActionSuccess('Pago liberado correctamente. El proyecto quedó cerrado/finalizado.');
      const refreshedProject = await projectService.getById(projectId);
      setProject(refreshedProject);
    } catch (err) {
      setActionError(parseApiError(err));
    } finally {
      setBusy(false);
    }
  }

  return (
    <Layout>
      <section className="page">
        <header className="page-header">
          <div className="page-header-row">
            <div>
              <h2>Pago simulado del proyecto</h2>
              <p className="page-subtitle">
                Gestiona el pago académico tipo escrow asociado al proyecto contratado.
              </p>
            </div>
            <Link to="/client/projects" className="btn btn-ghost">
              ← Mis proyectos
            </Link>
          </div>
        </header>

        <div className="alert alert-info" role="status">
          Este pago es simulado para fines académicos. No representa una transacción real.
        </div>

        {loading && <Loading text="Cargando pago simulado…" />}
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

        {!loading && !error && project && !payment && (
          <article className="detail-card">
            <h3>Este proyecto todavía no tiene pago simulado</h3>
            <p className="detail-desc">
              Crea un pago simulado después de aceptar una cotización. El sistema tomará el monto
              de la cotización aceptada y lo dejará en estado RETENIDO.
            </p>
            <div className="detail-actions">
              <button
                type="button"
                className="btn btn-primary"
                disabled={busy || !canCreatePayment(project)}
                onClick={handleCreate}
              >
                {busy ? 'Creando…' : 'Crear pago simulado'}
              </button>
              <Link to={`/client/projects/${project.id}/quotes`} className="btn btn-ghost">
                Ver cotizaciones
              </Link>
            </div>
            {!canCreatePayment(project) && (
              <p className="muted">
                El proyecto debe estar contratado antes de crear el pago simulado.
              </p>
            )}
          </article>
        )}

        {!loading && !error && payment && (
          <>
            {payment.estado === 'RETENIDO' && !hasApprovedEvidence && (
              <div className="alert alert-warning" role="status">
                Debes aprobar una evidencia antes de liberar el pago simulado.
              </div>
            )}

            {payment.estado === 'LIBERADO' && (
              <div className="alert alert-success" role="status">
                ✓ El pago ya fue liberado. El proyecto debe quedar cerrado/finalizado.
              </div>
            )}

            <div className="quote-list">
              <PaymentCard
                payment={payment}
                perspective="cliente"
                extraActions={
                  <>
                    <Link
                      to={`/client/projects/${payment.proyectoId}/evidences`}
                      className="btn btn-ghost"
                    >
                      Ver evidencias
                    </Link>
                    {payment.estado === 'RETENIDO' && hasApprovedEvidence && (
                      <button
                        type="button"
                        className="btn btn-primary"
                        disabled={busy}
                        onClick={handleRelease}
                      >
                        {busy ? 'Liberando…' : 'Liberar pago'}
                      </button>
                    )}
                  </>
                }
              />
            </div>
          </>
        )}
      </section>
    </Layout>
  );
}
