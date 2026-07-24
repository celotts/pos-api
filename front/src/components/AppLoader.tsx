import React, { useEffect, useState } from 'react';
import { useDispatch } from 'react-redux';
// import { restoreAuthState } from '../store/slices/authSlice'; // ELIMINADO: Ya no es necesario

interface AppLoaderProps {
  children: React.ReactNode;
}

const AppLoader: React.FC<AppLoaderProps> = ({ children }) => {
  const dispatch = useDispatch();
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // La lógica de restauración del estado ahora se maneja en el initialState de authSlice.
    // No es necesario despachar una acción aquí.
    // Si necesitas alguna inicialización asíncrona, este es el lugar.
    // Por ahora, simplemente marcamos como cargado.
    setLoading(false);
  }, [dispatch]);

  if (loading) {
    // Muestra un indicador de carga global mientras se verifica el estado
    return <div className="flex h-screen items-center justify-center">Cargando aplicación...</div>;
  }

  return <>{children}</>;
};

export default AppLoader;
