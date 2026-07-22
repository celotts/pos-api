import React from 'react';
import { useSelector } from 'react-redux';
import { Navigate, Outlet } from 'react-router-dom';
import { selectIsAuthenticated } from './authSlice';
import MainLayout from './MainLayout';

const ProtectedRoute: React.FC = () => {
    const isAuthenticated = useSelector(selectIsAuthenticated);
    console.log('ProtectedRoute - isAuthenticated:', isAuthenticated); // <-- AÑADIDO

    if (!isAuthenticated) {
        console.log('ProtectedRoute - Not authenticated, redirecting to /login'); // <-- AÑADIDO
        return <Navigate to="/login" replace />;
    }

    console.log('ProtectedRoute - Authenticated, rendering MainLayout'); // <-- AÑADIDO
    return <MainLayout><Outlet /></MainLayout>;
};

export default ProtectedRoute;
