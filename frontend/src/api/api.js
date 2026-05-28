import axios from 'axios';

/**
 * Instancia única de Axios para toda la app.
 *
 * baseURL "/api/v1" es relativa: en desarrollo Vite la enruta a
 * http://localhost:8080/api/v1 vía proxy (ver vite.config.js).
 * En producción debes servir el build estático desde el mismo
 * origen que el backend, o configurar CORS en Spring Boot.
 */
const api = axios.create({
  baseURL: '/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
});

/**
 * Interceptor de request: si hay token en localStorage, lo agrega
 * como "Authorization: Bearer <token>" en cada petición.
 */
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

/**
 * Interceptor de response: si el backend responde 401 (token inválido o
 * expirado), limpiamos la sesión y dejamos que el componente que invocó
 * la petición decida qué hacer (típicamente redirigir a /login).
 *
 * No usamos window.location aquí para no acoplar la capa de red con el
 * router; AuthContext y los componentes manejan la redirección.
 */
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('userId');
      localStorage.removeItem('email');
      localStorage.removeItem('nombre');
      localStorage.removeItem('rol');
    }
    return Promise.reject(error);
  }
);

export default api;
