import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// Configuración del servidor de desarrollo de Vite.
// El proxy redirige cualquier petición a "/api" hacia el backend Spring Boot
// en localhost:8080. Esto evita CORS en desarrollo SIN tocar el backend:
// el navegador ve solo una sola origin (localhost:5173) y el reenvío lo hace Vite.
//
// En el frontend usamos baseURL: "/api/v1" (ruta relativa). Vite la traduce a
// "http://localhost:8080/api/v1" automáticamente.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    open: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
    },
  },
});
