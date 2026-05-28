/**
 * Campo de formulario controlado con label, input/textarea/select
 * y soporte para mostrar error inline.
 *
 * Props:
 *  - id, name, label, type, value, onChange (controles base)
 *  - as: 'input' | 'textarea' | 'select' (default 'input')
 *  - error: string opcional
 *  - children: solo para 'select' (las <option>)
 *  - el resto se pasa al elemento (placeholder, min, step, rows, etc.)
 */
export default function FormField({
  id,
  name,
  label,
  as = 'input',
  error,
  children,
  ...rest
}) {
  const inputId = id || name;

  let control;
  if (as === 'textarea') {
    control = <textarea id={inputId} name={name} {...rest} />;
  } else if (as === 'select') {
    control = (
      <select id={inputId} name={name} {...rest}>
        {children}
      </select>
    );
  } else {
    control = <input id={inputId} name={name} {...rest} />;
  }

  return (
    <label className="field" htmlFor={inputId}>
      <span className="field-label">{label}</span>
      {control}
      {error && <span className="field-error">{error}</span>}
    </label>
  );
}
