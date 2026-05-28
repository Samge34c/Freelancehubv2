import { useCallback, useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import Layout from '../../components/Layout.jsx';
import Loading from '../../components/Loading.jsx';
import ErrorMessage from '../../components/ErrorMessage.jsx';
import EmptyState from '../../components/EmptyState.jsx';
import QuoteCard from '../../components/QuoteCard.jsx';
import StatusBadge from '../../components/StatusBadge.jsx';
import projectService from '../../api/projectService.js';
import quoteService from '../../api/quoteService.js';
import {
  formatCurrency,
  labelEstadoProyecto,
  parseApiError,
  variantEstadoProyecto,
} from '../../utils/format.js';

export default function ProjectQuotesPage() {
  const { projectId } = useParams();

  const [project, setProject] = useState(null);
  const [quotes, setQuotes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Estado del flujo "aceptar"
  const [acceptingId, setAcceptingId] = useState(null);
  const [acceptSuccess, setAcceptSuccess] = useState(null); // mensaje breve
  const [acceptError, setAcceptError] = useState(null);

  const load = useCallback(() => {
    setLoading(true);
    setError(null);
    setAcceptSuccess(null);
    setAcceptError(null);

    // Cargar en paralelo: detalle del proyecto + cotizaciones
    Promise.all([
      projectService.getById(projectId),
      quoteService.listByProject(projectId),
    ])
      .then(([proj, qs]) => {
        setProject(proj);
        setQuotes(Array.isArray(qs) ? qs : []);
      })
      .catch((err) => setError(parseApiError(err)))
      .finally(() => setLoading(false));
  }, [projectId]);

  useEffect(() => {
    load();
  }, [load]);

  async function handleAccept(quote) {
    setAcceptingId(quote.id);
    setAcceptError(null);
    setAcceptSuccess(null);
    try {
      await quoteService.accept(projectId, quote.id);
      // Tras aceptar, recargamos todo para reflejar:
      //  - la cotización aceptada → ACEPTADA
      //  - las otras PENDIENTES → RECHAZADA
      //  - el proyecto → EN_CONTRATO
      await load();
      setAcceptSuccess(
        `Cotización de ${quote.profesionalNombre || `#${quote.profesionalId}`} aceptada correctamente.`
      );
    } catch (err) {
      setAcceptError(parseApiError(err));
    } finally {
      setAcceptingId(null);
    }
  }

  return (
    <Layout>
      <section className="page">
        <header className="page-header">
          <div className="page-header-row">
            <div>
              <h2>Cotizaciones recibidas</h2>
              <p className="page-subtitle">
                Revisa y acepta la cotización que mejor se ajuste a tu proyecto.
              </p>
            </div>
            <Link to="/client/projects" className="btn btn-ghost">
              ← Mis proyectos
            </Link>
          </div>
        </header>

        {loading && <Loading text="Cargando cotizaciones…" />}
        <ErrorMessage message={error} onRetry={load} />

        {!loading && !error && project && (
          <div className="project-summary">
            <div className="project-summary-info">
              <h3>{project.titulo}</h3>
              <p className="muted">
                Presupuesto: {formatCurrency(project.presupuesto)} ·
                Categoría: {project.categoriaNombre || '—'}
              </p>
            </div>
            <StatusBadge
              label={labelEstadoProyecto(project.estado)}
              variant={variantEstadoProyecto(project.estado)}
            />
          </div>
        )}

        {acceptSuccess && (
          <div className="alert alert-success" role="status">
            ✓ {acceptSuccess}
          </div>
        )}

        {!loading && !error && project?.estado === 'EN_CONTRATO' && (
          <div className="alert alert-info" role="status">
            El proyecto ya está contratado. Puedes crear o revisar el pago simulado desde{' '}
            <Link to={`/client/projects/${project.id}/payment`}>Pago simulado</Link>.
          </div>
        )}
        <ErrorMessage message={acceptError} />

        {!loading && !error && quotes.length === 0 && (
          <EmptyState
            title="Este proyecto aún no tiene cotizaciones"
            description="Cuando un profesional envíe una cotización aparecerá aquí."
          />
        )}

        {!loading && !error && quotes.length > 0 && (
          <div className="quote-list">
            {quotes.map((q) => (
              <QuoteCard
                key={q.id}
                quote={q}
                perspective="cliente"
                onAccept={
                  // El botón solo aparece si la cotización está PENDIENTE
                  // Y el proyecto sigue ABIERTO (no tiene sentido aceptar otra
                  // si ya hay contrato).
                  project?.estado === 'ABIERTO' ? handleAccept : undefined
                }
                accepting={acceptingId === q.id}
              />
            ))}
          </div>
        )}
      </section>
    </Layout>
  );
}
