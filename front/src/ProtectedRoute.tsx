import React from 'react';
import { useSelector } from 'react-redux';
import { Navigate, Outlet } from 'react-router-dom';
import { selectIsAuthenticated } from '../features/auth/authSlice';
import MainLayout from '../components/layout/MainLayout';

const ProtectedRoute: React.FC = () => {
    const isAuthenticated = useSelector(selectIsAuthenticated);

    if (!isAuthenticated) {
        return <Navigate to="/login" replace />;
    }

    return <MainLayout><Outlet /></MainLayout>;
};

export default ProtectedRoute;