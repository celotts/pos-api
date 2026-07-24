import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from './hooks/useAuth';

interface ProtectedRouteProps {
  allowedRoles?: string[];
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ allowedRoles }) => {
    const { user } = useAuth();

    if (!user) {
        // Si no hay usuario, lo redirigimos al login.
        // El 'replace' evita que el usuario pueda volver a la página anterior con el botón de retroceso.
        return <Navigate to="/login" state={{ from: location.pathname }} replace />;
    }

    // Si se especifican roles permitidos, verificamos que el usuario tenga al menos uno.
    const hasRequiredRole = allowedRoles
        ? user.roles.some(role => allowedRoles.includes(role))
        : true; // Si no se especifican roles, solo se requiere autenticación.

    if (!hasRequiredRole) {
        // Si el usuario no tiene el rol requerido, lo redirigimos a una página de "no autorizado" o al inicio.
        return <Navigate to="/" replace />;
    }

    return <Outlet />;
};

export default ProtectedRoute;
