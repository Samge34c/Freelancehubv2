import { Link } from 'react-router-dom';
import Layout from '../components/Layout.jsx';
import { useAuth } from '../context/AuthContext.jsx';

const adminModules = [
  {
    title: 'Usuarios',
    text: 'Consulta clientes, profesionales y administradores registrados.',
  },
  {
    title: 'Proyectos',
    text: 'Revisa el estado general de los proyectos publicados en la plataforma.',
  },
  {
    title: 'Cotizaciones',
    text: 'Supervisa las cotizaciones enviadas y aceptadas dentro del sistema.',
  },
  {
    title: 'Evidencias',
    text: 'Consulta documentos de entrega enviados por profesionales.',
  },
  {
    title: 'Pagos simulados',
    text: 'Visualiza pagos retenidos, liberados o cancelados para control académico.',
  },
  {
    title: 'Arbitrajes',
    text: 'Gestiona disputas entre clientes y profesionales.',
  },
  {
    title: 'Categorías',
    text: 'Administra las categorías de proyectos disponibles.',
  },
  {
    title: 'Habilidades',
    text: 'Mantén el catálogo de habilidades para perfiles profesionales.',
  },
];

export default function AdminDashboard() {
  const { user } = useAuth();
  const nombre = user?.nombre || 'Administrador';

  return (
    <Layout>
      <section className="page">
        <header className="page-header">
          <h2>Bienvenido, {nombre}</h2>
          <p className="page-subtitle">
            Desde este panel podrás revisar la actividad general de FreelanceHub.
            Las opciones administrativas avanzadas quedan visibles como módulos
            próximos para mantener una navegación clara durante la sustentación.
          </p>
        </header>

        <div className="admin-summary-grid">
          <article className="admin-summary-card">
            <strong>Rol activo</strong>
            <span>Administrador</span>
          </article>
          <article className="admin-summary-card">
            <strong>Módulos actuales</strong>
            <span>Login, proyectos, cotizaciones, evidencias y pagos</span>
          </article>
          <article className="admin-summary-card">
            <strong>Estado</strong>
            <span>Panel administrativo en expansión</span>
          </article>
        </div>

        <div className="card-grid">
          {adminModules.map((module) => (
            <article className={module.title === 'Arbitrajes' ? 'card card-action' : 'card card-disabled'} key={module.title}>
              <h3>{module.title}</h3>
              <p>{module.text}</p>
              {module.title === 'Arbitrajes' ? (
                <Link to="/admin/arbitrations" className="btn btn-primary">
                  Revisar arbitrajes
                </Link>
              ) : (
                <span className="card-tag">Próximamente</span>
              )}
            </article>
          ))}
        </div>
      </section>
    </Layout>
  );
}
