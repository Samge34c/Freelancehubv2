import { useCallback, useEffect, useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import Layout from '../../components/Layout.jsx';
import Loading from '../../components/Loading.jsx';
import ErrorMessage from '../../components/ErrorMessage.jsx';
import EmptyState from '../../components/EmptyState.jsx';
import ProjectCard from '../../components/ProjectCard.jsx';
import projectService from '../../api/projectService.js';
import { parseApiError } from '../../utils/format.js';

function canViewEvidences(project) {
  return ['EN_CONTRATO', 'EN_PROCESO', 'EN_REVISION', 'CERRADO'].includes(project.estado);
}

export default function MyProjectsPage() {
  const location = useLocation();
  const justCreated = location.state?.created === true;
  const attachmentUploaded = location.state?.attachmentUploaded === true;
  const attachmentWarning = location.state?.attachmentWarning;

  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const load = useCallback(() => {
    setLoading(true);
    setError(null);
    projectService
      .listMy()
      .then((list) => setProjects(Array.isArray(list) ? list : []))
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
              <h2>Mis proyectos</h2>
              <p className="page-subtitle">
                Aquí ves los proyectos que has publicado.
              </p>
            </div>
            <div className="page-header-actions">
              <Link to="/client/projects/new" className="btn btn-primary">
                + Crear proyecto
              </Link>
            </div>
          </div>
        </header>

        {justCreated && (
          <div className="alert alert-success" role="status">
            ✓ Proyecto creado correctamente.
            {attachmentUploaded && ' Archivo adjunto subido correctamente.'}
          </div>
        )}

        {attachmentWarning && (
          <div className="alert alert-warning" role="alert">
            {attachmentWarning}
          </div>
        )}

        {loading && <Loading text="Cargando proyectos…" />}
        <ErrorMessage message={error} onRetry={load} />

        {!loading && !error && projects.length === 0 && (
          <EmptyState
            title="Aún no tienes proyectos"
            description="Crea tu primer proyecto para empezar a recibir cotizaciones."
            action={
              <Link to="/client/projects/new" className="btn btn-primary">
                + Crear proyecto
              </Link>
            }
          />
        )}

        {!loading && !error && projects.length > 0 && (
          <div className="card-grid">
            {projects.map((p) => (
              <ProjectCard
                key={p.id}
                project={p}
                actions={
                  <>
                    <Link
                      to={`/client/projects/${p.id}`}
                      className="btn btn-ghost"
                    >
                      Ver detalle
                    </Link>
                    <Link
                      to={`/client/projects/${p.id}/quotes`}
                      className="btn btn-primary"
                    >
                      Ver cotizaciones
                    </Link>
                    {canViewEvidences(p) && (
                      <>
                        <Link
                          to={`/client/projects/${p.id}/evidences`}
                          className="btn btn-ghost"
                        >
                          Ver evidencias
                        </Link>
                        <Link
                          to={`/client/projects/${p.id}/payment`}
                          className="btn btn-ghost"
                        >
                          Pago simulado
                        </Link>
                      </>
                    )}
                  </>
                }
              />
            ))}
          </div>
        )}
      </section>
    </Layout>
  );
}
