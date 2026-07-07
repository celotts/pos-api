import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000, // El frontend correrá en el puerto 3000
    proxy: {
      // Redirige las peticiones de /api a nuestro backend de Spring Boot
      '/api': {
        target: 'http://localhost:9090',
        changeOrigin: true,
        secure: false,
      },
    },
  },
})
