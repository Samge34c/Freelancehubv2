import api from './api.js';

/** Registro público de usuarios. El backend devuelve AuthResponse, pero por UX
 * redirigimos al login para que el usuario inicie sesión explícitamente. */
const authService = {
  registerClient(payload) {
    return api.post('/auth/register/client', payload).then((r) => r.data);
  },

  registerProfessional(payload) {
    return api.post('/auth/register/professional', payload).then((r) => r.data);
  },
};

export default authService;
