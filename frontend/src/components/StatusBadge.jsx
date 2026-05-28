/**
 * Píldora visual para estados (proyecto o cotización).
 * El color sale de la variante calculada en utils/format.js.
 */
export default function StatusBadge({ label, variant = 'neutral' }) {
  return <span className={`badge badge-${variant}`}>{label}</span>;
}
