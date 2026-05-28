import { createContext, useContext, useEffect, useState } from 'react';
import api from '../api/api.js';

const AuthContext = createContext(null);

/**
 * Provider de autenticación.
 * - Hidrata estado desde localStorage al montar (sobrevive a F5).
 * - Expone login(email, password) que llama al backend.
 * - Expone logout() que limpia todo y vuelve a /login.
 */
export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // Al montar la app, rehidratar sesión si hay token en localStorage.
  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      const userId = localStorage.getItem('userId');
      const email = localStorage.getItem('email');
      const nombre = localStorage.getItem('nombre');
      const rol = localStorage.getItem('rol');
      if (userId && email && rol) {
        setUser({ userId: Number(userId), email, nombre, rol });
      }
    }
    setLoading(false);
  }, []);

  /**
   * Llama al endpoint real /api/v1/auth/login.
   * Devuelve el objeto user en éxito, o lanza Error con mensaje legible.
   */
  async function login(email, password) {
    try {
      const { data } = await api.post('/auth/login', { email, password });
      // El AuthResponse del backend incluye: token, tokenType, userId, email, nombre, rol
      localStorage.setItem('token', data.token);
      localStorage.setItem('userId', String(data.userId));
      localStorage.setItem('email', data.email);
      localStorage.setItem('nombre', data.nombre || '');
      localStorage.setItem('rol', data.rol);

      const session = {
        userId: data.userId,
        email: data.email,
        nombre: data.nombre,
        rol: data.rol,
      };
      setUser(session);
      return session;
    } catch (err) {
      // Mensaje legible para el usuario.
      const status = err.response?.status;
      const apiMessage =
        err.response?.data?.mensaje ||
        err.response?.data?.message ||
        err.response?.data?.error;

      if (status === 401 || status === 403) {
        throw new Error('Credenciales incorrectas.');
      }
      if (status >= 400 && status < 500) {
        throw new Error(apiMessage || 'No fue posible iniciar sesión.');
      }
      if (status >= 500) {
        throw new Error('El servidor reportó un error. Intenta de nuevo.');
      }
      // Sin response = problema de red o backend caído
      throw new Error(
        'No se pudo conectar con el servidor. ¿Está corriendo el backend en :8080?'
      );
    }
  }

  function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
    localStorage.removeItem('email');
    localStorage.removeItem('nombre');
    localStorage.removeItem('rol');
    setUser(null);
  }

  const value = {
    user,
    loading,
    isAuthenticated: Boolean(user),
    login,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error('useAuth debe usarse dentro de <AuthProvider>');
  }
  return ctx;
}

/**
 * Helper: dado un rol, devuelve la ruta del dashboard correspondiente.
 */
export function dashboardPathFor(rol) {
  switch (rol) {
    case 'CLIENTE':
      return '/client';
    case 'PROFESIONAL':
      return '/professional';
    case 'ADMIN':
      return '/admin';
    default:
      return '/login';
  }
}
