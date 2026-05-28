import { Link } from 'react-router-dom';
import Layout from '../components/Layout.jsx';
import { useAuth } from '../context/AuthContext.jsx';

export default function ProfessionalDashboard() {
  const { user } = useAuth();
  const nombre = user?.nombre || 'Profesional';

  return (
    <Layout>
      <section className="page">
        <header className="page-header">
          <h2>Bienvenido, {nombre}</h2>
          <p className="page-subtitle">
            Desde este panel podrás ver proyectos abiertos, enviar cotizaciones,
            consultar pagos retenidos y subir evidencias de entrega. Usa el menú
            superior para navegar por tus módulos.
          </p>
        </header>

        <div className="card-grid">
          <article className="card card-action">
            <h3>Proyectos abiertos</h3>
            <p>Explora oportunidades publicadas por clientes.</p>
            <Link to="/professional/projects/open" className="btn btn-primary">
              Ver proyectos abiertos
            </Link>
          </article>

          <article className="card card-action">
            <h3>Mis cotizaciones</h3>
            <p>Revisa el estado de las cotizaciones que has enviado.</p>
            <Link to="/professional/quotes" className="btn btn-primary">
              Ver mis cotizaciones
            </Link>
          </article>

          <article className="card card-action">
            <h3>Mis evidencias</h3>
            <p>Consulta documentos enviados y sube nuevas evidencias de entrega.</p>
            <Link to="/professional/evidences" className="btn btn-primary">
              Ver evidencias
            </Link>
          </article>

          <article className="card card-action">
            <h3>Pagos simulados</h3>
            <p>Consulta pagos retenidos o liberados de tus proyectos contratados.</p>
            <Link to="/professional/payments" className="btn btn-primary">
              Ver pagos
            </Link>
          </article>

          <article className="card card-action">
            <h3>Arbitrajes</h3>
            <p>Responde desacuerdos abiertos por clientes y adjunta soportes.</p>
            <Link to="/professional/arbitrations" className="btn btn-primary">
              Ver arbitrajes
            </Link>
          </article>
        </div>
      </section>
    </Layout>
  );
}
