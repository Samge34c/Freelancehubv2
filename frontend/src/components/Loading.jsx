/**
 * Indicador de carga simple y consistente.
 * - "inline": dentro de un botón o fila pequeña.
 * - "block": ocupa toda la página (centra vertical + horizontalmente).
 */
export default function Loading({ text = 'Cargando…', variant = 'block' }) {
  if (variant === 'inline') {
    return <span className="loading-inline">{text}</span>;
  }
  return (
    <div className="loading-block">
      <div className="spinner" aria-hidden="true" />
      <p>{text}</p>
    </div>
  );
}
