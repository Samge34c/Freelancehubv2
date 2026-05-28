import Navbar from './Navbar.jsx';

/**
 * Envoltorio compartido para dashboards autenticados.
 * - Renderiza el Navbar arriba.
 * - El contenido pasa como children.
 */
export default function Layout({ children }) {
  return (
    <div className="app-shell">
      <Navbar />
      <main className="app-main">{children}</main>
    </div>
  );
}
