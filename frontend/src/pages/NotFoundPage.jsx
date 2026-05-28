import { Link } from 'react-router-dom';
import { useAuth, dashboardPathFor } from '../context/AuthContext.jsx';

export default function NotFoundPage() {
  const { isAuthenticated, user } = useAuth();
  const home = isAuthenticated && user ? dashboardPathFor(user.rol) : '/login';

  return (
    <div className="not-found-shell">
      <div className="not-found-card">
        <h1>404</h1>
        <p>La página que buscas no existe.</p>
        <Link to={home} className="btn btn-primary">
          Volver al inicio
        </Link>
      </div>
    </div>
  );
}
