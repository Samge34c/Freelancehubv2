import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';

/**
 * Wrapper de ruta privada.
 *
 * - Mientras AuthContext está hidratando (loading=true), no decide nada
 *   para evitar un parpadeo a /login.
 * - Si no hay sesión, redirige a /login conservando la ruta intentada
 *   (state.from) por si más adelante queremos retomar tras login.
 * - Si hay sesión, renderiza el contenido protegido.
 */
export default function PrivateRoute({ children }) {
  const { isAuthenticated, loading } = useAuth();
  const location = useLocation();

  if (loading) {
    return <div className="centered-loader">Cargando…</div>;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  return children;
}
