import { Link } from 'react-router-dom';
import StatusBadge from './StatusBadge.jsx';
import {
  formatCurrency,
  formatDate,
  formatDays,
  labelEstadoProyecto,
  variantEstadoProyecto,
} from '../utils/format.js';

/**
 * Tarjeta de proyecto. Acepta una o más acciones (links/botones).
 *
 * Props:
 *  - project: ProjectResponse del backend
 *  - actions: ReactNode con los botones/links a renderizar al final
 */
export default function ProjectCard({ project, actions }) {
  const descCorta =
    project.descripcion && project.descripcion.length > 160
      ? project.descripcion.slice(0, 160) + '…'
      : project.descripcion;

  return (
    <article className="project-card">
      <header className="project-card-head">
        <h3 className="project-card-title">{project.titulo}</h3>
        <StatusBadge
          label={labelEstadoProyecto(project.estado)}
          variant={variantEstadoProyecto(project.estado)}
        />
      </header>

      <p className="project-card-desc">{descCorta || '—'}</p>

      <dl className="project-card-meta">
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
          <dt>Creado</dt>
          <dd>{formatDate(project.fechaCreacion)}</dd>
        </div>
      </dl>

      {project.clienteNombre && (
        <p className="project-card-client">
          <span className="muted">Cliente:</span> {project.clienteNombre}
        </p>
      )}

      {actions && <div className="project-card-actions">{actions}</div>}
    </article>
  );
}
