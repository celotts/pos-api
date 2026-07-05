import React from 'react';
import { useSelector } from 'react-redux';
import { Navigate, Outlet } from 'react-router-dom';
import { selectCurrentUser } from '../store/slices/authSlice';

interface ProtectedRouteProps {
  allowedRoles: string[];
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ allowedRoles }) => {
  const user = useSelector(selectCurrentUser);

  if (!user) {
    // Esto no debería pasar si la ruta ya está protegida por autenticación,
    // pero es una salvaguarda.
    return <Navigate to="/login" replace />;
  }

  // Comprueba si el usuario tiene al menos uno de los roles permitidos
  const hasRequiredRole = user.roles.some(role => allowedRoles.includes(role));

  if (!hasRequiredRole) {
    // Si no tiene el rol, lo redirigimos a una página de "No Autorizado"
    // o de vuelta al dashboard. Por ahora, al dashboard.
    return <Navigate to="/" replace />;
  }

  // Si tiene el rol, renderizamos el contenido de la ruta.
  return <Outlet />;
};

export default ProtectedRoute;
