import { Navigate } from 'react-router-dom';
import { useAuth, dashboardPathFor } from '../context/AuthContext.jsx';

/**
 * Wrapper de ruta restringida por rol.
 *
 * Uso:
 *   <RoleRoute allow={["CLIENTE"]}>
 *     <ClientDashboard />
 *   </RoleRoute>
 *
 * - Asume que el usuario ya está autenticado (se compone con PrivateRoute).
 * - Si el rol del usuario NO está en allow, lo redirige a SU propio dashboard.
 *   Decisión de diseño: no mostramos página de "Acceso denegado" porque la
 *   experiencia es más fluida regresando al dashboard correcto. Mostramos
 *   un mensaje breve por un instante para que el usuario entienda.
 */
export default function RoleRoute({ allow, children }) {
  const { user } = useAuth();

  if (!user) {
    // No debería llegar aquí si está compuesto con PrivateRoute, pero por seguridad:
    return <Navigate to="/login" replace />;
  }

  if (!allow.includes(user.rol)) {
    return <Navigate to={dashboardPathFor(user.rol)} replace />;
  }

  return children;
}
