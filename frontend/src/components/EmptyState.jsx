/**
 * Vista para listas vacías. Muestra un título, descripción opcional
 * y opcionalmente una acción (botón) para invitar al usuario a algo.
 */
export default function EmptyState({ title, description, action }) {
  return (
    <div className="empty-state">
      <h3>{title}</h3>
      {description && <p>{description}</p>}
      {action}
    </div>
  );
}
