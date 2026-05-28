import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Layout from '../../components/Layout.jsx';
import Loading from '../../components/Loading.jsx';
import ErrorMessage from '../../components/ErrorMessage.jsx';
import EmptyState from '../../components/EmptyState.jsx';
import ProjectCard from '../../components/ProjectCard.jsx';
import StatusBadge from '../../components/StatusBadge.jsx';
import projectService from '../../api/projectService.js';
import quoteService from '../../api/quoteService.js';
import {
  formatCurrency,
  labelEstadoCotizacion,
  parseApiError,
  variantEstadoCotizacion,
} from '../../utils/format.js';

export default function ReceivedQuotesPage() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const load = useCallback(() => {
    setLoading(true);
    setError(null);
    projectService
      .listMy()
      .then(async (projects) => {
        const safeProjects = Array.isArray(projects) ? projects : [];
        const withQuotes = await Promise.all(
          safeProjects.map(async (project) => {
            const quotes = await quoteService.listByProject(project.id);
            return {
              project,
              quotes: Array.isArray(quotes) ? quotes : [],
            };
          })
        );
        setItems(withQuotes.filter((item) => item.quotes.length > 0));
      })
      .catch((err) => setError(parseApiError(err)))
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  return (
    <Layout>
      <section className="page">
        <header className="page-header">
          <div className="page-header-row">
            <div>
              <h2>Cotizaciones recibidas</h2>
              <p className="page-subtitle">
                Aquí solo aparecen proyectos que ya recibieron al menos una cotización.
              </p>
            </div>
            <Link to="/client/projects" className="btn btn-ghost">
              Ver todos mis proyectos
            </Link>
          </div>
        </header>

        {loading && <Loading text="Cargando cotizaciones recibidas…" />}
        <ErrorMessage message={error} onRetry={load} />

        {!loading && !error && items.length === 0 && (
          <EmptyState
            title="Aún no tienes cotizaciones recibidas"
            description="Tus proyectos aparecerán aquí cuando algún profesional envíe una cotización."
            action={
              <Link to="/client/projects" className="btn btn-primary">
                Ver mis proyectos
              </Link>
            }
          />
        )}

        {!loading && !error && items.length > 0 && (
          <div className="card-grid">
            {items.map(({ project, quotes }) => {
              const acceptedQuote = quotes.find((q) => q.estado === 'ACEPTADA');
              const bestQuote = acceptedQuote || quotes[0];
              return (
                <ProjectCard
                  key={project.id}
                  project={project}
                  actions={
                    <>
                      <div className="quote-summary-pill">
                        <strong>{quotes.length}</strong>{' '}
                        {quotes.length === 1 ? 'cotización recibida' : 'cotizaciones recibidas'}
                        {bestQuote && (
                          <span>
                            · Desde {formatCurrency(bestQuote.precio)} ·{' '}
                            <StatusBadge
                              label={labelEstadoCotizacion(bestQuote.estado)}
                              variant={variantEstadoCotizacion(bestQuote.estado)}
                            />
                          </span>
                        )}
                      </div>
                      <Link
                        to={`/client/projects/${project.id}/quotes`}
                        className="btn btn-primary"
                      >
                        Revisar cotizaciones
                      </Link>
                    </>
                  }
                />
              );
            })}
          </div>
        )}
      </section>
    </Layout>
  );
}
