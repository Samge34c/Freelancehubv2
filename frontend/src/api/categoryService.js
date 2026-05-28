import api from './api.js';

/**
 * Catálogo de categorías. Cualquier usuario autenticado puede
 * leerlas; solo ADMIN puede modificarlas (no expuesto en este frontend).
 */
const categoryService = {
  /** @returns CategoryResponse[] */
  listAll() {
    return api.get('/categories').then((r) => r.data);
  },

  getById(id) {
    return api.get(`/categories/${id}`).then((r) => r.data);
  },
};

export default categoryService;
