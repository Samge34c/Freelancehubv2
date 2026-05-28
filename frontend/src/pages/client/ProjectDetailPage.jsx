import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import Layout from '../../components/Layout.jsx';
import Loading from '../../components/Loading.jsx';
import ErrorMessage from '../../components/ErrorMessage.jsx';
import StatusBadge from '../../components/StatusBadge.jsx';
import AttachmentList from '../../components/AttachmentList.jsx';
import projectService from '../../api/projectService.js';
import {
  formatCurrency,
  formatDateTime,
  formatDays,
  labelEstadoProyecto,
  parseApiError,
  variantEstadoProyecto,
} from '../../utils/format.js';

function canViewEvidences(project) {
  return ['EN_CONTRATO', 'EN_PROCESO', 'EN_REVISION', 'CERRADO'].includes(project.estado);
}

export default function ProjectDetailPage() {
  const { projectId } = useParams();

  const [project, setProject] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    let alive = true;
    setLoading(true);
    setError(null);
    projectService
      .getById(projectId)
      .then((p) => alive && setProject(p))
      .catch((err) => alive && setError(parseApiError(err)))
      .finally(() => alive && setLoading(false));
    return () => {
      alive = false;
    };
  }, [projectId]);

  return (
    <Layout>
      <section className="page">
        <header className="page-header">
          <div className="page-header-row">
            <div>
              <h2>Detalle del proyecto</h2>
              <p className="page-subtitle">
                Información completa de tu proyecto publicado.
              </p>
            </div>
            <Link to="/client/projects" className="btn btn-ghost">
              ← Mis proyectos
            </Link>
          </div>
        </header>

        {loading && <Loading text="Cargando proyecto…" />}
        <ErrorMessage message={error} />

        {!loading && !error && project && (
          <>
          <article className="detail-card">
            <header className="detail-head">
              <h3>{project.titulo}</h3>
              <StatusBadge
                label={labelEstadoProyecto(project.estado)}
                variant={variantEstadoProyecto(project.estado)}
              />
            </header>

            <p className="detail-desc">{project.descripcion}</p>

            <dl className="detail-meta">
              <div>
                <dt>Presupuesto</dt>
                <dd>{formatCurrency(project.presupuesto)}</dd>
              </div>
              <div>
                <dt>Plazo</dt>
                <dd>{formatDays(project.plazo)}</dd>
              </div>
              <div>
                <dt>Categoría</dt>
                <dd>{project.categoriaNombre || '—'}</dd>
              </div>
              <div>
                <dt>Cliente</dt>
                <dd>{project.clienteNombre || '—'}</dd>
              </div>
              <div>
                <dt>Creado</dt>
                <dd>{formatDateTime(project.fechaCreacion)}</dd>
              </div>
            </dl>

            <div className="detail-actions">
              <Link
                to={`/client/projects/${project.id}/quotes`}
                className="btn btn-primary"
              >
                Ver cotizaciones recibidas
              </Link>
              {canViewEvidences(project) && (
                <>
                  <Link
                    to={`/client/projects/${project.id}/evidences`}
                    className="btn btn-ghost"
                  >
                    Ver evidencias
                  </Link>
                  <Link
                    to={`/client/projects/${project.id}/payment`}
                    className="btn btn-ghost"
                  >
                    Pago simulado
                  </Link>
                </>
              )}
            </div>
          </article>

          <AttachmentList projectId={project.id} />
          </>
        )}
      </section>
    </Layout>
  );
}
