import { Fragment, useCallback, useEffect, useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import Layout from '../../components/Layout.jsx';
import Loading from '../../components/Loading.jsx';
import ErrorMessage from '../../components/ErrorMessage.jsx';
import EmptyState from '../../components/EmptyState.jsx';
import QuoteCard from '../../components/QuoteCard.jsx';
import StatusBadge from '../../components/StatusBadge.jsx';
import quoteService from '../../api/quoteService.js';
import paymentService from '../../api/paymentService.js';
import {
  labelEstadoPago,
  parseApiError,
  variantEstadoPago,
} from '../../utils/format.js';

export default function MyQuotesPage() {
  const location = useLocation();
  const justCreated = location.state?.created === true;

  const [quotes, setQuotes] = useState([]);
  const [paymentsByProject, setPaymentsByProject] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const load = useCallback(() => {
    setLoading(true);
    setError(null);
    quoteService
      .listMine()
      .then(async (list) => {
        const safeList = Array.isArray(list) ? list : [];
        setQuotes(safeList);

        const acceptedQuotes = safeList.filter((q) => q.estado === 'ACEPTADA');
        const uniqueProjectIds = [...new Set(acceptedQuotes.map((q) => q.proyectoId))];
        const entries = await Promise.all(
          uniqueProjectIds.map(async (projectId) => {
            try {
              const payment = await paymentService.getForProject(projectId);
              return [projectId, payment];
            } catch (err) {
              if (err.response?.status === 404) return [projectId, null];
              throw err;
            }
          })
        );
        setPaymentsByProject(Object.fromEntries(entries));
      })
      .catch((err) => setError(parseApiError(err)))
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  function renderAcceptedQuoteActions(quote) {
    if (quote.estado !== 'ACEPTADA') return null;

    const payment = paymentsByProject[quote.proyectoId];

    if (payment === undefined) {
      return <span className="card-tag">Verificando pago…</span>;
    }

    if (payment === null) {
      return (
        <Fragment>
          <span className="card-tag card-tag-warn">Esperando pago simulado del cliente</span>
          <Link to="/professional/payments" className="btn btn-ghost">
            Ver pagos
          </Link>
        </Fragment>
      );
    }

    if (payment.estado === 'RETENIDO') {
      return (
        <Fragment>
          <StatusBadge
            label={`Pago ${labelEstadoPago(payment.estado)}`}
            variant={variantEstadoPago(payment.estado)}
          />
          <Link
            to={`/professional/projects/${quote.proyectoId}/evidences/new`}
            className="btn btn-primary"
          >
            Subir evidencia
          </Link>
          <Link to="/professional/payments" className="btn btn-ghost">
            Ver pagos
          </Link>
        </Fragment>
      );
    }

    if (payment.estado === 'LIBERADO') {
      return (
        <Fragment>
          <StatusBadge
            label={`Pago ${labelEstadoPago(payment.estado)}`}
            variant={variantEstadoPago(payment.estado)}
          />
          <span className="card-tag">Entrega finalizada</span>
          <Link to="/professional/payments" className="btn btn-ghost">
            Ver pagos
          </Link>
        </Fragment>
      );
    }

    return (
      <Fragment>
        <StatusBadge
          label={`Pago ${labelEstadoPago(payment.estado)}`}
          variant={variantEstadoPago(payment.estado)}
        />
        <Link to="/professional/payments" className="btn btn-ghost">
          Ver pagos
        </Link>
      </Fragment>
    );
  }

  return (
    <Layout>
      <section className="page">
        <header className="page-header">
          <div className="page-header-row">
            <div>
              <h2>Mis cotizaciones</h2>
              <p className="page-subtitle">
                Estado de las cotizaciones que has enviado.
              </p>
            </div>
            <div className="page-header-actions">
              <Link to="/professional/projects/open" className="btn btn-primary">
                Buscar proyectos
              </Link>
            </div>
          </div>
        </header>

        {justCreated && (
          <div className="alert alert-success" role="status">
            ✓ Cotización enviada correctamente.
          </div>
        )}

        {loading && <Loading text="Cargando cotizaciones…" />}
        <ErrorMessage message={error} onRetry={load} />

        {!loading && !error && quotes.length === 0 && (
          <EmptyState
            title="Aún no has enviado cotizaciones"
            description="Explora los proyectos abiertos y envía tu primera propuesta."
            action={
              <Link to="/professional/projects/open" className="btn btn-primary">
                Ver proyectos abiertos
              </Link>
            }
          />
        )}

        {!loading && !error && quotes.length > 0 && (
          <div className="quote-list">
            {quotes.map((q) => (
              <QuoteCard
                key={q.id}
                quote={q}
                perspective="profesional"
                extraActions={renderAcceptedQuoteActions(q)}
              />
            ))}
          </div>
        )}
      </section>
    </Layout>
  );
}
