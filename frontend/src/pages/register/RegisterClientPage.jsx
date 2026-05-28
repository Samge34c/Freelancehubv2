import { Link, useNavigate } from 'react-router-dom';
import { useState } from 'react';
import FormField from '../../components/FormField.jsx';
import Loading from '../../components/Loading.jsx';
import ErrorMessage from '../../components/ErrorMessage.jsx';
import authService from '../../api/authService.js';
import { parseApiError } from '../../utils/format.js';

const INITIAL = {
  nombre: '',
  email: '',
  password: '',
  confirmPassword: '',
  telefono: '',
  empresa: '',
  razonSocial: '',
};

export default function RegisterClientPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState(INITIAL);
  const [errors, setErrors] = useState({});
  const [apiError, setApiError] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  function update(field) {
    return (e) => {
      setForm((prev) => ({ ...prev, [field]: e.target.value }));
      setErrors((prev) => ({ ...prev, [field]: undefined }));
    };
  }

  function validate() {
    const errs = {};
    if (!form.nombre.trim()) errs.nombre = 'El nombre es obligatorio.';
    if (!form.email.trim()) errs.email = 'El correo es obligatorio.';
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email.trim()))
      errs.email = 'Ingresa un correo válido.';
    if (!form.password) errs.password = 'La contraseña es obligatoria.';
    else if (form.password.length < 8) errs.password = 'La contraseña debe tener al menos 8 caracteres.';
    if (form.confirmPassword !== form.password) errs.confirmPassword = 'Las contraseñas no coinciden.';

    setErrors(errs);
    return Object.keys(errs).length === 0;
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setApiError(null);
    if (!validate()) return;

    setSubmitting(true);
    try {
      await authService.registerClient({
        nombre: form.nombre.trim(),
        email: form.email.trim(),
        password: form.password,
        telefono: form.telefono.trim() || null,
        empresa: form.empresa.trim() || null,
        razonSocial: form.razonSocial.trim() || null,
      });
      navigate('/login', { state: { registered: 'client' }, replace: true });
    } catch (err) {
      setApiError(parseApiError(err));
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="login-shell">
      <div className="login-card register-card">
        <header className="login-header">
          <h1 className="brand">Registro cliente</h1>
          <p className="brand-sub">Crea una cuenta para publicar proyectos y recibir cotizaciones.</p>
        </header>

        <form className="login-form" onSubmit={handleSubmit} noValidate>
          <FormField
            name="nombre"
            label="Nombre completo"
            type="text"
            value={form.nombre}
            onChange={update('nombre')}
            disabled={submitting}
            error={errors.nombre}
            required
          />
          <FormField
            name="email"
            label="Correo electrónico"
            type="email"
            value={form.email}
            onChange={update('email')}
            disabled={submitting}
            error={errors.email}
            required
          />
          <FormField
            name="telefono"
            label="Teléfono"
            type="tel"
            value={form.telefono}
            onChange={update('telefono')}
            disabled={submitting}
            placeholder="Opcional"
          />
          <FormField
            name="empresa"
            label="Empresa"
            type="text"
            value={form.empresa}
            onChange={update('empresa')}
            disabled={submitting}
            placeholder="Opcional"
          />
          <FormField
            name="razonSocial"
            label="Razón social"
            type="text"
            value={form.razonSocial}
            onChange={update('razonSocial')}
            disabled={submitting}
            placeholder="Opcional"
          />
          <FormField
            name="password"
            label="Contraseña"
            type="password"
            value={form.password}
            onChange={update('password')}
            disabled={submitting}
            error={errors.password}
            required
          />
          <FormField
            name="confirmPassword"
            label="Confirmar contraseña"
            type="password"
            value={form.confirmPassword}
            onChange={update('confirmPassword')}
            disabled={submitting}
            error={errors.confirmPassword}
            required
          />

          <ErrorMessage message={apiError} />

          <button type="submit" className="btn btn-primary btn-block" disabled={submitting}>
            {submitting ? <Loading variant="inline" text="Creando cuenta…" /> : 'Crear cuenta de cliente'}
          </button>
        </form>

        <footer className="login-footer register-footer">
          <small>¿Ya tienes cuenta? <Link to="/login">Inicia sesión</Link></small>
        </footer>
      </div>
    </div>
  );
}
