import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Layout from '../../components/Layout.jsx';
import Loading from '../../components/Loading.jsx';
import ErrorMessage from '../../components/ErrorMessage.jsx';
import EmptyState from '../../components/EmptyState.jsx';
import PaymentCard from '../../components/PaymentCard.jsx';
import paymentService from '../../api/paymentService.js';
import { parseApiError } from '../../utils/format.js';

export default function ProfessionalPaymentsPage() {
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
              <h2>Pagos simulados</h2>
              <p className="page-subtitle">
                Consulta pagos retenidos o liberados asociados a tus cotizaciones aceptadas.
              </p>
            </div>
            <Link to="/professional/quotes" className="btn btn-primary">
              Mis cotizaciones
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
            description="Cuando un cliente acepte tu cotización y cree el pago simulado, aparecerá aquí."
            action={
              <Link to="/professional/quotes" className="btn btn-primary">
                Ver mis cotizaciones
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
                perspective="profesional"
                extraActions={
                  p.estado === 'RETENIDO' ? (
                    <Link
                      to={`/professional/projects/${p.proyectoId}/evidences/new`}
                      className="btn btn-primary"
                    >
                      Subir evidencia
                    </Link>
                  ) : (
                    <span className="card-tag">Pago liberado / entrega finalizada</span>
                  )
                }
              />
            ))}
          </div>
        )}
      </section>
    </Layout>
  );
}
