import { NavLink, useNavigate } from 'react-router-dom';
import { dashboardPathFor, useAuth } from '../context/AuthContext.jsx';

const roleLabel = {
  CLIENTE: 'Cliente',
  PROFESIONAL: 'Profesional',
  ADMIN: 'Administrador',
};

const menuByRole = {
  CLIENTE: [
    { label: 'Dashboard', to: '/client', exact: true },
    { label: 'Crear proyecto', to: '/client/projects/new' },
    { label: 'Mis proyectos', to: '/client/projects' },
    {
      label: 'Cotizaciones recibidas',
      to: '/client/quotes-received',
      helper: 'Muestra solo proyectos con cotizaciones recibidas',
    },
    {
      label: 'Evidencias',
      to: '/client/projects',
      helper: 'Selecciona un proyecto contratado para ver evidencias',
    },
    { label: 'Pagos simulados', to: '/client/payments' },
    { label: 'Arbitrajes', to: '/client/arbitrations' },
  ],
  PROFESIONAL: [
    { label: 'Dashboard', to: '/professional', exact: true },
    { label: 'Proyectos abiertos', to: '/professional/projects/open' },
    { label: 'Mis cotizaciones', to: '/professional/quotes' },
    { label: 'Mis evidencias', to: '/professional/evidences' },
    { label: 'Pagos simulados', to: '/professional/payments' },
    { label: 'Arbitrajes', to: '/professional/arbitrations' },
  ],
  ADMIN: [
    { label: 'Dashboard', to: '/admin', exact: true },
    { label: 'Usuarios', disabled: true, tag: 'Próximamente' },
    { label: 'Proyectos', disabled: true, tag: 'Próximamente' },
    { label: 'Cotizaciones', disabled: true, tag: 'Próximamente' },
    { label: 'Evidencias', disabled: true, tag: 'Próximamente' },
    { label: 'Pagos simulados', disabled: true, tag: 'Próximamente' },
    { label: 'Arbitrajes', to: '/admin/arbitrations' },
    { label: 'Categorías', disabled: true, tag: 'Próximamente' },
    { label: 'Habilidades', disabled: true, tag: 'Próximamente' },
  ],
};

function MenuItem({ item }) {
  if (item.disabled) {
    return (
      <span
        className="navbar-link navbar-link-disabled"
        title={item.tag || 'Próximamente'}
        aria-disabled="true"
      >
        {item.label}
        {item.tag && <small>{item.tag}</small>}
      </span>
    );
  }

  return (
    <NavLink
      to={item.to}
      end={item.exact}
      className={({ isActive }) =>
        isActive ? 'navbar-link navbar-link-active' : 'navbar-link'
      }
      title={item.helper || item.label}
    >
      {item.label}
    </NavLink>
  );
}

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate('/login', { replace: true });
  }

  const currentRoleLabel = roleLabel[user?.rol] || user?.rol;
  const menuItems = user ? menuByRole[user.rol] || [] : [];
  const dashboardPath = user ? dashboardPathFor(user.rol) : '/login';

  return (
    <nav className="navbar" aria-label="Navegación principal">
      <div className="navbar-inner">
        <NavLink to={dashboardPath} className="navbar-brand" aria-label="Ir al dashboard">
          <span className="brand-dot" aria-hidden="true" />
          <span className="brand-name">FreelanceHub</span>
        </NavLink>

        {user && (
          <div className="navbar-user">
            <div className="navbar-user-meta">
              <strong>{user.nombre || user.email}</strong>
              <span className="navbar-role">{currentRoleLabel}</span>
            </div>
            <button
              type="button"
              className="btn btn-ghost btn-logout"
              onClick={handleLogout}
            >
              Cerrar sesión
            </button>
          </div>
        )}
      </div>

      {user && menuItems.length > 0 && (
        <div className="navbar-menu-wrap">
          <div className="navbar-menu" role="menubar" aria-label={`Menú ${currentRoleLabel}`}>
            {menuItems.map((item) => (
              <MenuItem key={`${item.label}-${item.to || item.tag}`} item={item} />
            ))}
          </div>
        </div>
      )}
    </nav>
  );
}
