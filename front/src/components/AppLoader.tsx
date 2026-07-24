import React, { useEffect, useState } from 'react';
import { useDispatch } from 'react-redux';
import { restoreAuthState } from '../store/slices/authSlice';

interface AppLoaderProps {
  children: React.ReactNode;
}

const AppLoader: React.FC<AppLoaderProps> = ({ children }) => {
  const dispatch = useDispatch();
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Intenta restaurar el estado de autenticación al cargar la app
    dispatch(restoreAuthState());
    // Damos por terminada la carga inicial. El resto de la app
    // reaccionará al estado de Redux (ya sea con usuario o sin él).
    setLoading(false);
  }, [dispatch]);

  if (loading) {
    // Muestra un indicador de carga global mientras se verifica el estado
    return <div className="flex h-screen items-center justify-center">Cargando aplicación...</div>;
  }

  return <>{children}</>;
};

export default AppLoader;