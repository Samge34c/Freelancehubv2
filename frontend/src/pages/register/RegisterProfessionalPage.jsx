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
  areaExperiencia: '',
  biografia: '',
  tarifaPromedio: '',
  certificaciones: '',
};

export default function RegisterProfessionalPage() {
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
    if (!form.areaExperiencia.trim()) errs.areaExperiencia = 'Indica tu área o especialidad.';
    if (!form.biografia.trim()) errs.biografia = 'Escribe una biografía corta.';
    else if (form.biografia.trim().length < 20) errs.biografia = 'La biografía debe tener al menos 20 caracteres.';
    if (form.tarifaPromedio) {
      const tarifa = Number(form.tarifaPromedio);
      if (Number.isNaN(tarifa) || tarifa < 0) errs.tarifaPromedio = 'La tarifa debe ser un número positivo.';
    }

    setErrors(errs);
    return Object.keys(errs).length === 0;
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setApiError(null);
    if (!validate()) return;

    setSubmitting(true);
    try {
      await authService.registerProfessional({
        nombre: form.nombre.trim(),
        email: form.email.trim(),
        password: form.password,
        telefono: form.telefono.trim() || null,
        areaExperiencia: form.areaExperiencia.trim(),
        biografia: form.biografia.trim(),
        tarifaPromedio: form.tarifaPromedio ? Number(form.tarifaPromedio) : null,
        certificaciones: form.certificaciones.trim() || null,
      });
      navigate('/login', { state: { registered: 'professional' }, replace: true });
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
          <h1 className="brand">Registro profesional</h1>
          <p className="brand-sub">Crea una cuenta para cotizar proyectos y entregar evidencias.</p>
        </header>

        <form className="login-form" onSubmit={handleSubmit} noValidate>
          <div className="form-row">
            <FormField name="nombre" label="Nombre completo" type="text" value={form.nombre} onChange={update('nombre')} disabled={submitting} error={errors.nombre} required />
            <FormField name="telefono" label="Teléfono" type="tel" value={form.telefono} onChange={update('telefono')} disabled={submitting} placeholder="Opcional" />
          </div>
          <FormField name="email" label="Correo electrónico" type="email" value={form.email} onChange={update('email')} disabled={submitting} error={errors.email} required />
          <FormField name="areaExperiencia" label="Área de experiencia" type="text" value={form.areaExperiencia} onChange={update('areaExperiencia')} disabled={submitting} error={errors.areaExperiencia} placeholder="Ej. Desarrollo web, diseño UI, marketing digital" required />
          <FormField name="biografia" label="Biografía profesional" as="textarea" rows={4} value={form.biografia} onChange={update('biografia')} disabled={submitting} error={errors.biografia} placeholder="Describe tu experiencia, servicios y forma de trabajo." required />
          <FormField name="tarifaPromedio" label="Tarifa promedio (COP)" type="number" min="0" step="1000" value={form.tarifaPromedio} onChange={update('tarifaPromedio')} disabled={submitting} error={errors.tarifaPromedio} placeholder="Opcional" />
          <FormField name="certificaciones" label="Diploma, tarjeta profesional o certificaciones" as="textarea" rows={3} value={form.certificaciones} onChange={update('certificaciones')} disabled={submitting} placeholder="Describe tus certificaciones o número de tarjeta profesional. El archivo físico se puede validar en una fase posterior." />
          <div className="form-row">
            <FormField name="password" label="Contraseña" type="password" value={form.password} onChange={update('password')} disabled={submitting} error={errors.password} required />
            <FormField name="confirmPassword" label="Confirmar contraseña" type="password" value={form.confirmPassword} onChange={update('confirmPassword')} disabled={submitting} error={errors.confirmPassword} required />
          </div>

          <ErrorMessage message={apiError} />

          <button type="submit" className="btn btn-primary btn-block" disabled={submitting}>
            {submitting ? <Loading variant="inline" text="Creando cuenta…" /> : 'Crear cuenta profesional'}
          </button>
        </form>

        <footer className="login-footer register-footer">
          <small>¿Ya tienes cuenta? <Link to="/login">Inicia sesión</Link></small>
        </footer>
      </div>
    </div>
  );
}
