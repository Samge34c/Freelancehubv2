import StatusBadge from './StatusBadge.jsx';
import {
  formatCurrency,
  formatDateTime,
  labelEstadoPago,
  variantEstadoPago,
} from '../utils/format.js';

export default function PaymentCard({ payment, perspective = 'cliente', extraActions }) {
  const released = payment.estado === 'LIBERADO';
  const retained = payment.estado === 'RETENIDO';
  const returned = payment.estado === 'DEVUELTO';

  return (
    <article className={`quote-card ${released ? 'quote-card-accepted' : ''}`}>
      <header className="quote-card-head">
        <div className="quote-card-id">
          <span className="muted">Proyecto:</span>{' '}
          <strong>{payment.proyectoTitulo || `#${payment.proyectoId}`}</strong>
        </div>
        <StatusBadge
          label={labelEstadoPago(payment.estado)}
          variant={variantEstadoPago(payment.estado)}
        />
      </header>

      <p className="quote-card-desc">
        {payment.descripcion || 'Pago simulado asociado al contrato del proyecto.'}
      </p>

      <dl className="quote-card-meta">
        <div>
          <dt>Monto</dt>
          <dd>{formatCurrency(payment.monto)}</dd>
        </div>
        <div>
          <dt>Referencia</dt>
          <dd>{payment.referencia || '—'}</dd>
        </div>
        <div>
          <dt>Creado</dt>
          <dd>{formatDateTime(payment.fechaCreacion)}</dd>
        </div>
        <div>
          <dt>{payment.estado === 'DEVUELTO' ? 'Devuelto' : 'Liberado'}</dt>
          <dd>{formatDateTime(payment.fechaLiberacion)}</dd>
        </div>
        <div>
          <dt>{perspective === 'cliente' ? 'Profesional' : 'Cliente'}</dt>
          <dd>
            {perspective === 'cliente'
              ? payment.profesionalNombre || `#${payment.profesionalId}`
              : payment.clienteNombre || `#${payment.clienteId}`}
          </dd>
        </div>
      </dl>

      {payment.notaSimulacion && (
        <div className="simulation-note">
          {payment.notaSimulacion}
        </div>
      )}

      {extraActions && <div className="quote-card-actions">{extraActions}</div>}

      {retained && perspective === 'profesional' && (
        <div className="quote-card-accepted-banner quote-card-retained-banner">
          Pago retenido: puedes subir la evidencia para que el cliente la revise.
        </div>
      )}

      {released && (
        <div className="quote-card-accepted-banner">
          ✓ Este pago simulado fue liberado al profesional.
        </div>
      )}

      {returned && (
        <div className="alert alert-warning">
          Pago devuelto al cliente por resolución de arbitraje.
        </div>
      )}
    </article>
  );
}
