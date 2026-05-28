import StatusBadge from './StatusBadge.jsx';
import {
  formatCurrency,
  formatDateTime,
  formatDays,
  labelEstadoCotizacion,
  variantEstadoCotizacion,
} from '../utils/format.js';

/**
 * Tarjeta de cotización. Sirve para dos vistas:
 *  - CLIENTE: ver cotizaciones recibidas (muestra nombre del profesional + botón aceptar)
 *  - PROFESIONAL: ver mis cotizaciones (muestra título del proyecto)
 *
 * Props:
 *  - quote: QuoteResponse del backend
 *  - perspective: 'cliente' | 'profesional'
 *  - onAccept: callback opcional (solo cliente). Si se pasa Y la cotización está PENDIENTE,
 *              se muestra el botón "Aceptar".
 *  - accepting: boolean opcional (true mientras se acepta, deshabilita botón)
 */
export default function QuoteCard({
  quote,
  perspective = 'cliente',
  onAccept,
  accepting = false,
  extraActions,
}) {
  const isAccepted = quote.estado === 'ACEPTADA';
  const showAcceptButton =
    perspective === 'cliente' &&
    typeof onAccept === 'function' &&
    quote.estado === 'PENDIENTE';

  return (
    <article className={`quote-card ${isAccepted ? 'quote-card-accepted' : ''}`}>
      <header className="quote-card-head">
        <div className="quote-card-id">
          {perspective === 'cliente' ? (
            <>
              <span className="muted">Profesional:</span>{' '}
              <strong>{quote.profesionalNombre || `#${quote.profesionalId}`}</strong>
            </>
          ) : (
            <>
              <span className="muted">Proyecto:</span>{' '}
              <strong>{quote.proyectoTitulo || `#${quote.proyectoId}`}</strong>
            </>
          )}
        </div>
        <StatusBadge
          label={labelEstadoCotizacion(quote.estado)}
          variant={variantEstadoCotizacion(quote.estado)}
        />
      </header>

      <p className="quote-card-desc">{quote.descripcion || '—'}</p>

      <dl className="quote-card-meta">
        <div>
          <dt>Precio</dt>
          <dd>{formatCurrency(quote.precio)}</dd>
        </div>
        <div>
          <dt>Plazo</dt>
          <dd>{formatDays(quote.plazo)}</dd>
        </div>
        <div>
          <dt>Enviada</dt>
          <dd>{formatDateTime(quote.fechaEnvio)}</dd>
        </div>
      </dl>

      {(showAcceptButton || extraActions) && (
        <div className="quote-card-actions">
          {showAcceptButton && (
            <button
              type="button"
              className="btn btn-primary"
              disabled={accepting}
              onClick={() => onAccept(quote)}
            >
              {accepting ? 'Aceptando…' : 'Aceptar cotización'}
            </button>
          )}
          {extraActions}
        </div>
      )}

      {isAccepted && (
        <div className="quote-card-accepted-banner">
          ✓ Esta cotización fue aceptada.
        </div>
      )}
    </article>
  );
}
