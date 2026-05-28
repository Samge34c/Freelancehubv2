import { useEffect, useState } from 'react';
import Layout from '../../components/Layout.jsx';
import Loading from '../../components/Loading.jsx';
import ErrorMessage from '../../components/ErrorMessage.jsx';
import EmptyState from '../../components/EmptyState.jsx';
import ArbitrationCard from '../../components/ArbitrationCard.jsx';
import arbitrationService from '../../api/arbitrationService.js';
import { parseApiError } from '../../utils/format.js';

export default function AdminArbitrationsPage() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  function load() {
    setLoading(true);
    setError(null);
    arbitrationService.listAllForAdmin()
      .then((data) => setItems(Array.isArray(data) ? data : []))
      .catch((err) => setError(parseApiError(err)))
      .finally(() => setLoading(false));
  }

  useEffect(() => { load(); }, []);

  return (
    <Layout>
      <section className="page">
        <header className="page-header">
          <h2>Arbitrajes</h2>
          <p className="page-subtitle">Casos de disputa entre clientes y profesionales.</p>
        </header>
        {loading && <Loading text="Cargando arbitrajes…" />}
        <ErrorMessage message={error} onRetry={load} />
        {!loading && !error && items.length === 0 && <EmptyState title="No hay arbitrajes" description="Cuando un cliente solicite revisión aparecerá aquí." />}
        {!loading && !error && items.length > 0 && (
          <div className="quote-list">
            {items.map((a) => <ArbitrationCard key={a.id} arbitration={a} perspective="admin" />)}
          </div>
        )}
      </section>
    </Layout>
  );
}
