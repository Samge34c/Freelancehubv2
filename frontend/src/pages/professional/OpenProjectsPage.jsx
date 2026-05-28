import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Layout from '../../components/Layout.jsx';
import Loading from '../../components/Loading.jsx';
import ErrorMessage from '../../components/ErrorMessage.jsx';
import EmptyState from '../../components/EmptyState.jsx';
import ProjectCard from '../../components/ProjectCard.jsx';
import FormField from '../../components/FormField.jsx';
import projectService from '../../api/projectService.js';
import categoryService from '../../api/categoryService.js';
import { parseApiError } from '../../utils/format.js';

export default function OpenProjectsPage() {
  const [projects, setProjects] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Filtros (locales; se aplican cuando el usuario presiona "Aplicar filtros")
  const [filters, setFilters] = useState({
    categoryId: '',
    minBudget: '',
    maxBudget: '',
  });

  // Cargar categorías una sola vez
  useEffect(() => {
    let alive = true;
    categoryService
      .listAll()
      .then((list) => {
        if (!alive) return;
        const active = Array.isArray(list)
          ? list.filter((c) => c.activo !== false)
          : [];
        setCategories(active);
      })
      .catch(() => {
        /* si las categorías fallan, el listado sin filtro sigue funcionando */
      });
    return () => {
      alive = false;
    };
  }, []);

  const load = useCallback(
    (currentFilters = filters) => {
      setLoading(true);
      setError(null);

      // Construir parámetros enviables al backend
      const params = {};
      if (currentFilters.categoryId)
        params.categoryId = Number(currentFilters.categoryId);
      if (currentFilters.minBudget !== '' && currentFilters.minBudget != null)
        params.minBudget = Number(currentFilters.minBudget);
      if (currentFilters.maxBudget !== '' && currentFilters.maxBudget != null)
        params.maxBudget = Number(currentFilters.maxBudget);

      projectService
        .listOpen(params)
        .then((list) => setProjects(Array.isArray(list) ? list : []))
        .catch((err) => setError(parseApiError(err)))
        .finally(() => setLoading(false));
    },
    [filters]
  );

  useEffect(() => {
    // Carga inicial sin filtros
    load({ categoryId: '', minBudget: '', maxBudget: '' });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  function updateFilter(field) {
    return (e) => setFilters((p) => ({ ...p, [field]: e.target.value }));
  }

  function applyFilters(e) {
    e.preventDefault();
    load(filters);
  }

  function clearFilters() {
    const cleared = { categoryId: '', minBudget: '', maxBudget: '' };
    setFilters(cleared);
    load(cleared);
  }

  return (
    <Layout>
      <section className="page">
        <header className="page-header">
          <div className="page-header-row">
            <div>
              <h2>Proyectos abiertos</h2>
              <p className="page-subtitle">
                Explora oportunidades publicadas por clientes y envía tu cotización.
              </p>
            </div>
            <Link to="/professional" className="btn btn-ghost">
              ← Volver
            </Link>
          </div>
        </header>

        <form className="filter-bar" onSubmit={applyFilters}>
          <FormField
            name="categoryId"
            label="Categoría"
            as="select"
            value={filters.categoryId}
            onChange={updateFilter('categoryId')}
          >
            <option value="">Todas</option>
            {categories.map((c) => (
              <option key={c.id} value={c.id}>
                {c.nombre}
              </option>
            ))}
          </FormField>

          <FormField
            name="minBudget"
            label="Presupuesto mínimo"
            type="number"
            min="0"
            step="1000"
            value={filters.minBudget}
            onChange={updateFilter('minBudget')}
            placeholder="0"
          />

          <FormField
            name="maxBudget"
            label="Presupuesto máximo"
            type="number"
            min="0"
            step="1000"
            value={filters.maxBudget}
            onChange={updateFilter('maxBudget')}
            placeholder="∞"
          />

          <div className="filter-bar-actions">
            <button type="submit" className="btn btn-primary">
              Aplicar filtros
            </button>
            <button type="button" className="btn btn-ghost" onClick={clearFilters}>
              Limpiar
            </button>
          </div>
        </form>

        {loading && <Loading text="Cargando proyectos…" />}
        <ErrorMessage message={error} onRetry={() => load(filters)} />

        {!loading && !error && projects.length === 0 && (
          <EmptyState
            title="No hay proyectos abiertos"
            description="Por ahora no hay oportunidades disponibles que coincidan con los filtros."
          />
        )}

        {!loading && !error && projects.length > 0 && (
          <div className="card-grid">
            {projects.map((p) => (
              <ProjectCard
                key={p.id}
                project={p}
                actions={
                  <Link
                    to={`/professional/projects/${p.id}`}
                    className="btn btn-primary"
                  >
                    Ver y cotizar
                  </Link>
                }
              />
            ))}
          </div>
        )}
      </section>
    </Layout>
  );
}
