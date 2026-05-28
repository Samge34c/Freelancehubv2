import { Link } from 'react-router-dom';
import Layout from '../components/Layout.jsx';
import { useAuth } from '../context/AuthContext.jsx';

export default function ClientDashboard() {
  const { user } = useAuth();
  const nombre = user?.nombre || 'Cliente';

  return (
    <Layout>
      <section className="page">
        <header className="page-header">
          <h2>Bienvenido, {nombre}</h2>
          <p className="page-subtitle">
            Desde este panel podrás crear proyectos, revisar cotizaciones,
            aprobar entregas y gestionar pagos simulados. Usa el menú superior
            para moverte por los módulos principales de cliente.
          </p>
        </header>

        <div className="card-grid">
          <article className="card card-action">
            <h3>Crear proyecto</h3>
            <p>Publica una nueva solicitud para que los profesionales coticen.</p>
            <Link to="/client/projects/new" className="btn btn-primary">
              Crear proyecto
            </Link>
          </article>

          <article className="card card-action">
            <h3>Mis proyectos</h3>
            <p>Consulta tus proyectos, evidencias, cotizaciones y pagos asociados.</p>
            <Link to="/client/projects" className="btn btn-primary">
              Ver mis proyectos
            </Link>
          </article>

          <article className="card card-action">
            <h3>Cotizaciones recibidas</h3>
            <p>Consulta solo los proyectos que ya recibieron propuestas de profesionales.</p>
            <Link to="/client/quotes-received" className="btn btn-primary">
              Ver cotizaciones
            </Link>
          </article>

          <article className="card card-action">
            <h3>Evidencias de entrega</h3>
            <p>Revisa y aprueba los documentos enviados por el profesional contratado.</p>
            <Link to="/client/projects" className="btn btn-ghost">
              Seleccionar proyecto
            </Link>
          </article>

          <article className="card card-action">
            <h3>Pagos simulados</h3>
            <p>Visualiza y libera pagos académicos asociados a tus proyectos contratados.</p>
            <Link to="/client/payments" className="btn btn-primary">
              Ver pagos
            </Link>
          </article>

          <article className="card card-action">
            <h3>Arbitrajes</h3>
            <p>Gestiona desacuerdos entre cliente y profesional con revisión de administrador.</p>
            <Link to="/client/arbitrations" className="btn btn-primary">
              Ver arbitrajes
            </Link>
          </article>
        </div>
      </section>
    </Layout>
  );
}
