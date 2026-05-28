import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Layout from '../../components/Layout.jsx';
import Loading from '../../components/Loading.jsx';
import ErrorMessage from '../../components/ErrorMessage.jsx';
import EmptyState from '../../components/EmptyState.jsx';
import PaymentCard from '../../components/PaymentCard.jsx';
import paymentService from '../../api/paymentService.js';
import { parseApiError } from '../../utils/format.js';

export default function MyPaymentsPage() {
  const [payments, setPayments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const load = useCallback(() => {
    setLoading(true);
    setError(null);
    paymentService
      .listMine()
      .then((list) => setPayments(Array.isArray(list) ? list : []))
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
              <h2>Mis pagos simulados</h2>
              <p className="page-subtitle">
                Consulta pagos retenidos o liberados de tus proyectos contratados.
              </p>
            </div>
            <Link to="/client/projects" className="btn btn-primary">
              Ver mis proyectos
            </Link>
          </div>
        </header>

        <div className="alert alert-info" role="status">
          Estos pagos son simulados para fines académicos. No representan transacciones reales.
        </div>

        {loading && <Loading text="Cargando pagos simulados…" />}
        <ErrorMessage message={error} onRetry={load} />

        {!loading && !error && payments.length === 0 && (
          <EmptyState
            title="Aún no tienes pagos simulados"
            description="Acepta una cotización y crea un pago simulado desde el detalle del proyecto."
            action={
              <Link to="/client/projects" className="btn btn-primary">
                Ir a mis proyectos
              </Link>
            }
          />
        )}

        {!loading && !error && payments.length > 0 && (
          <div className="quote-list">
            {payments.map((p) => (
              <PaymentCard
                key={p.id}
                payment={p}
                perspective="cliente"
                extraActions={
                  <Link
                    to={`/client/projects/${p.proyectoId}/payment`}
                    className="btn btn-primary"
                  >
                    Gestionar pago
                  </Link>
                }
              />
            ))}
          </div>
        )}
      </section>
    </Layout>
  );
}
