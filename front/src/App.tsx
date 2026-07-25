import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import ProtectedRoute from './components/ProtectedRoute'; // Asegúrate que la ruta es correcta
import { useAuth } from './hooks/useAuth';
import MainLayout from './layouts/MainLayout';
import ProductsPage from './pages/ProductsPage';
import CategoriesPage from './pages/CategoriesPage';
import RolesPage from './pages/RolesPage';
import SuppliersPage from './pages/SuppliersPage';
import TaxesPage from './pages/TaxesPage';
import UsersPage from './pages/UsersPage';
import PurchasesPage from './pages/PurchasesPage';
import ShiftsPage from './pages/ShiftsPage';
import AccountsPayablePage from './pages/AccountsPayablePage';
import CustomersPage from './pages/CustomersPage';
import CashAccountsPage from './pages/CashAccountsPage';

import { Toaster } from 'react-hot-toast';

const LoginRedirect: React.FC = () => {
    const { user } = useAuth();
    // Si el usuario ya está logueado, lo redirigimos a la página de productos.
    // Si no, le mostramos la página de login.
    return user ? <Navigate to="/products" replace /> : <LoginPage />;
};

const App: React.FC = () => {
    return (
        <div>
            <Router>
                <Routes>
                    <Route path="/login" element={<LoginRedirect />} />
                    
                    {/* Rutas Protegidas */}
                    <Route path="/" element={<MainLayout />}>
                        <Route index element={<Navigate to="/products" replace />} />
                        
                        {/* Rutas para todos los usuarios autenticados */}
                        <Route element={<ProtectedRoute />}>
                            <Route path="products" element={<ProductsPage />} />
                            <Route path="purchases" element={<PurchasesPage />} />
                        </Route>
                        
                        {/* Rutas para ADMIN y EDITOR */}
                        <Route element={<ProtectedRoute allowedRoles={['ADMIN', 'EDITOR']} />}>
                            <Route path="categories" element={<CategoriesPage />} />
                            <Route path="suppliers" element={<SuppliersPage />} />
                            <Route path="taxes" element={<TaxesPage />} />
                            <Route path="customers" element={<CustomersPage />} />
                        </Route>
                        
                        {/* Rutas solo para ADMIN */}
                        <Route element={<ProtectedRoute allowedRoles={['ADMIN']} />}>
                            <Route path="users" element={<UsersPage />} />
                            <Route path="roles" element={<RolesPage />} />
                            <Route path="shifts" element={<ShiftsPage />} />
                            <Route path="accounts-payable" element={<AccountsPayablePage />} />
                            <Route path="cash-accounts" element={<CashAccountsPage />} />
                        </Route>
                    </Route>

                    {/* Redirect any non-matching routes to the root. 
                        The logic inside the app will then handle redirection to /login or /products.
                    */}
                    <Route path="*" element={<Navigate to="/" replace />} />
                </Routes>
            </Router>
            <Toaster position="bottom-right" />
        </div>
    );
};

export default App;