import React from 'react';
import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

interface ProtectedRouteProps {
  allowedRoles?: string[];
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ allowedRoles }) => {
  const { user } = useAuth();
  const location = useLocation();

  if (!user) {
    // Si el usuario no está autenticado, lo redirigimos al login.
    // Guardamos la ubicación a la que intentaba acceder para redirigirlo allí después del login.
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  // Si se especifican roles permitidos, verificamos si el usuario tiene al menos uno.
  // Si `allowedRoles` está vacío o no se define, solo se requiere autenticación.
  const hasRequiredRole =
    !allowedRoles || allowedRoles.length === 0
      ? true
      : user.roles.some(role => allowedRoles.includes(role));

  if (!hasRequiredRole) {
    // Si el usuario no tiene el rol necesario, lo redirigimos a la página principal.
    // Podrías crear una página de "Acceso no autorizado" aquí.
    return <Navigate to="/" replace />;
  }

  // Si tiene el rol, renderizamos el contenido de la ruta.
  return <Outlet />;
};

export default ProtectedRoute;
