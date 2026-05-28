import StatusBadge from './StatusBadge.jsx';
import {
  formatDateTime,
  labelEstadoEvidencia,
  variantEstadoEvidencia,
} from '../utils/format.js';

/**
 * Tarjeta visual para evidencias de entrega.
 * - perspective='cliente': muestra el profesional que subió la evidencia.
 * - perspective='profesional': muestra el proyecto asociado.
 */
export default function EvidenceCard({
  evidence,
  perspective = 'cliente',
  onDownload,
  onApprove,
  onReject,
  busy = false,
}) {
  const showClientActions =
    perspective === 'cliente' && evidence.estado === 'ENVIADA';

  return (
    <article className={`evidence-card evidence-card-${evidence.estado?.toLowerCase?.() || 'neutral'}`}>
      <header className="evidence-card-head">
        <div>
          <h3 className="evidence-card-title">
            {perspective === 'cliente'
              ? evidence.nombreArchivo
              : evidence.proyectoTitulo || `Proyecto #${evidence.proyectoId}`}
          </h3>
          <p className="muted">
            {perspective === 'cliente' ? (
              <>Profesional: {evidence.profesionalNombre || `#${evidence.profesionalId}`}</>
            ) : (
              <>Archivo: {evidence.nombreArchivo}</>
            )}
          </p>
        </div>
        <StatusBadge
          label={labelEstadoEvidencia(evidence.estado)}
          variant={variantEstadoEvidencia(evidence.estado)}
        />
      </header>

      <p className="evidence-card-desc">{evidence.descripcion || '—'}</p>

      <dl className="evidence-card-meta">
        <div>
          <dt>Subida</dt>
          <dd>{formatDateTime(evidence.fechaSubida)}</dd>
        </div>
        <div>
          <dt>Tipo</dt>
          <dd>{evidence.tipoArchivo || '—'}</dd>
        </div>
      </dl>

      <div className="evidence-card-actions">
        <button
          type="button"
          className="btn btn-ghost"
          onClick={() => onDownload?.(evidence)}
          disabled={busy}
        >
          Descargar
        </button>

        {showClientActions && (
          <>
            <button
              type="button"
              className="btn btn-primary"
              onClick={() => onApprove?.(evidence)}
              disabled={busy}
            >
              Aprobar
            </button>
            <button
              type="button"
              className="btn btn-ghost"
              onClick={() => onReject?.(evidence)}
              disabled={busy}
            >
              Rechazar
            </button>
          </>
        )}
      </div>
    </article>
  );
}
