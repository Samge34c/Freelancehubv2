import api from './api.js';

/**
 * Wrapper sobre los endpoints de proyectos del backend.
 * Mantenemos las firmas explícitas para que cada página sepa
 * exactamente qué datos manda al servidor.
 */
const projectService = {
  /**
   * CLIENTE: crear un proyecto.
   * @param {{categoriaId:number, titulo:string, descripcion:string, presupuesto:number, plazo:number}} payload
   * @returns ProjectResponse
   */
  create(payload) {
    return api.post('/projects', payload).then((r) => r.data);
  },

  /**
   * CLIENTE: listar mis proyectos.
   * @returns ProjectResponse[]
   */
  listMy() {
    return api.get('/projects/my').then((r) => r.data);
  },

  /**
   * PROFESIONAL: listar proyectos abiertos.
   * Filtros opcionales: categoryId, minBudget, maxBudget.
   *
   * Estrictos sobre los tipos: solo se incluyen los filtros en la query
   * cuando son números FINITOS válidos. Esto evita enviar valores como
   * Infinity, NaN o strings vacíos al backend, que provocarían 400.
   *
   * @param {{categoryId?:number, minBudget?:number, maxBudget?:number}} filters
   */
  listOpen(filters = {}) {
    const params = {};

    const cat = Number(filters.categoryId);
    if (Number.isFinite(cat) && cat > 0) params.categoryId = cat;

    const min = Number(filters.minBudget);
    if (Number.isFinite(min) && min >= 0) params.minBudget = min;

    const max = Number(filters.maxBudget);
    if (Number.isFinite(max) && max >= 0) params.maxBudget = max;

    return api.get('/projects/open', { params }).then((r) => r.data);
  },

  /**
   * Cualquier rol con autorización: obtener detalle de proyecto.
   * El backend valida la autorización por rol.
   */
  getById(projectId) {
    return api.get(`/projects/${projectId}`).then((r) => r.data);
  },
};

export default projectService;
