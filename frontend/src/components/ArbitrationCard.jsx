import { Link } from 'react-router-dom';
import StatusBadge from './StatusBadge.jsx';
import {
  formatDateTime,
  labelEstadoArbitraje,
  variantEstadoArbitraje,
} from '../utils/format.js';

export default function ArbitrationCard({ arbitration, perspective = 'cliente', extraActions }) {
  return (
    <article className="quote-card">
      <header className="quote-card-head">
        <div className="quote-card-id">
          <span className="muted">Proyecto:</span>{' '}
          <strong>{arbitration.proyectoTitulo || `#${arbitration.proyectoId}`}</strong>
        </div>
        <StatusBadge
          label={labelEstadoArbitraje(arbitration.estado)}
          variant={variantEstadoArbitraje(arbitration.estado)}
        />
      </header>

      <p className="quote-card-desc">
        <strong>Motivo del cliente:</strong> {arbitration.motivoCliente || '—'}
      </p>

      {arbitration.respuestaProfesional && (
        <p className="quote-card-desc">
          <strong>Respuesta profesional:</strong> {arbitration.respuestaProfesional}
        </p>
      )}

      {arbitration.decisionAdmin && (
        <p className="quote-card-desc">
          <strong>Decisión admin:</strong> {arbitration.decisionAdmin}
        </p>
      )}

      <dl className="quote-card-meta">
        <div>
          <dt>Cliente</dt>
          <dd>{arbitration.clienteNombre || `#${arbitration.clienteId}`}</dd>
        </div>
        <div>
          <dt>Profesional</dt>
          <dd>{arbitration.profesionalNombre || `#${arbitration.profesionalId}`}</dd>
        </div>
        <div>
          <dt>Creado</dt>
          <dd>{formatDateTime(arbitration.fechaCreacion)}</dd>
        </div>
        <div>
          <dt>Respuesta</dt>
          <dd>{formatDateTime(arbitration.fechaRespuestaProfesional)}</dd>
        </div>
        <div>
          <dt>Resolución</dt>
          <dd>{formatDateTime(arbitration.fechaResolucion)}</dd>
        </div>
      </dl>

      <div className="quote-card-actions">
        {perspective === 'admin' && (
          <Link to={`/admin/arbitrations/${arbitration.id}`} className="btn btn-primary">
            Revisar caso
          </Link>
        )}
        {extraActions}
      </div>
    </article>
  );
}
