import { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth, dashboardPathFor } from '../context/AuthContext.jsx';

export default function LoginPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const { login, isAuthenticated, user, loading } = useAuth();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);

  // Si ya está autenticado y entra a /login, redirigir según rol.
  useEffect(() => {
    if (!loading && isAuthenticated && user) {
      navigate(dashboardPathFor(user.rol), { replace: true });
    }
  }, [loading, isAuthenticated, user, navigate]);

  async function handleSubmit(event) {
    event.preventDefault();
    setError(null);

    if (!email.trim() || !password) {
      setError('Por favor ingresa email y contraseña.');
      return;
    }

    setSubmitting(true);
    try {
      const session = await login(email.trim(), password);
      navigate(dashboardPathFor(session.rol), { replace: true });
    } catch (err) {
      setError(err.message || 'No fue posible iniciar sesión.');
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="login-shell">
      <div className="login-card">
        <header className="login-header">
          <h1 className="brand">FreelanceHub</h1>
          <p className="brand-sub">Plataforma para conectar clientes y profesionales</p>
        </header>

        {location.state?.registered && (
          <div className="alert alert-success" role="status">
            ✓ Cuenta creada correctamente. Ya puedes iniciar sesión.
          </div>
        )}

        <form onSubmit={handleSubmit} className="login-form" noValidate>
          <label className="field">
            <span className="field-label">Correo electrónico</span>
            <input
              type="email"
              autoComplete="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="tucorreo@dominio.com"
              disabled={submitting}
              required
            />
          </label>

          <label className="field">
            <span className="field-label">Contraseña</span>
            <input
              type="password"
              autoComplete="current-password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Tu contraseña"
              disabled={submitting}
              required
            />
          </label>

          {error && (
            <div className="alert alert-error" role="alert">
              {error}
            </div>
          )}

          <button
            type="submit"
            className="btn btn-primary btn-block"
            disabled={submitting}
          >
            {submitting ? 'Ingresando…' : 'Iniciar sesión'}
          </button>
        </form>

        <footer className="login-footer register-links">
          <small>¿Aún no tienes cuenta?</small>
          <div className="register-link-row">
            <Link to="/register/client" className="btn btn-ghost btn-small">
              Crear cuenta como cliente
            </Link>
            <Link to="/register/professional" className="btn btn-ghost btn-small">
              Crear cuenta como profesional
            </Link>
          </div>
        </footer>
      </div>
    </div>
  );
}
