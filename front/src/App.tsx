import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import ProtectedRoute from './ProtectedRoute';
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
import CashAccountsPage from './pages/CashAccountsPage';

const App: React.FC = () => {
    return (
        <Router>
            <Routes>
                <Route path="/login" element={<LoginPage />} />

                <Route element={<ProtectedRoute />}>
                    <Route path="/" element={<MainLayout />}>
                        <Route index element={<Navigate to="/products" replace />} />
                        <Route path="products" element={<ProductsPage />} />
                        <Route path="purchases" element={<PurchasesPage />} />
                        <Route path="categories" element={<CategoriesPage />} />
                        <Route path="suppliers" element={<SuppliersPage />} />
                        <Route path="taxes" element={<TaxesPage />} />
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
    );
};

export default App;