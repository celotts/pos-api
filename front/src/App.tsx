import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import ProductList from './features/products/ProductList'; // Esta ruta ya era correcta
import LoginPage from './pages/LoginPage'; // Esta ruta ya era correcta
import ProtectedRoute from './router/ProtectedRoute';

const App: React.FC = () => {
    return (
        <Router>
            <Routes>
                <Route path="/login" element={<LoginPage />} />
                
                {/* Rutas Protegidas */}
                <Route element={<ProtectedRoute />}>
                    <Route path="/products" element={<ProductList />} />
                    {/* Aquí irán otras rutas protegidas como /categories, /sales, etc. */}
                </Route>
                
                <Route path="*" element={<Navigate to="/products" replace />} />
            </Routes>
        </Router>
    );
};

export default App;