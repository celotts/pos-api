import { configureStore } from '@reduxjs/toolkit';
import authReducer from './slices/authSlice';
import productReducer from './slices/productSlice';
import customerReducer from './slices/customerSlice'; // 1. Importar el reducer de clientes

export const store = configureStore({
  reducer: {
    auth: authReducer,
    products: productReducer,
    customers: customerReducer, // 2. Añadir el reducer al store
    // Aquí añadiremos otros reducers a medida que los creemos
  },
  // Habilitar Redux DevTools en desarrollo
  devTools: process.env.NODE_ENV !== 'production',
});

// Tipos para usar en toda la aplicación
export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
