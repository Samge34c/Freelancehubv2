/**
 * Muestra un mensaje de error con estilo de alerta.
 * Si no hay mensaje, no renderiza nada (más cómodo en JSX condicional).
 */
export default function ErrorMessage({ message, onRetry }) {
  if (!message) return null;
  return (
    <div className="alert alert-error" role="alert">
      <span>{message}</span>
      {onRetry && (
        <button type="button" className="btn-link" onClick={onRetry}>
          Reintentar
        </button>
      )}
    </div>
  );
}
