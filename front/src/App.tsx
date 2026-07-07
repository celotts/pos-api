import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { useSelector } from 'react-redux';
import MainLayout from './layouts/MainLayout';
import LoginPage from './pages/LoginPage';
import CategoriesPage from './pages/CategoriesPage';
import RolesPage from './pages/RolesPage';
import SuppliersPage from './pages/SuppliersPage';
import TaxesPage from './pages/TaxesPage';
import ProductsPage from './pages/ProductsPage';
import UsersPage from './pages/UsersPage';
import { selectIsAuthenticated } from './store/slices/authSlice';
import ProtectedRoute from './components/ProtectedRoute';
import { ROLES } from './utils/roles';

const DashboardPage = () => <h1 className="text-3xl font-bold">Welcome to the Dashboard</h1>;

function App() {
  const isAuthenticated = useSelector(selectIsAuthenticated);

  return (
    <BrowserRouter>
      <Routes>
        {/* La página de Login es pública */}
        <Route path="/login" element={!isAuthenticated ? <LoginPage /> : <Navigate to="/" />} />

        {/* Todas las demás rutas están protegidas */}
        <Route path="/" element={isAuthenticated ? <MainLayout /> : <Navigate to="/login" />}>
          <Route index element={<DashboardPage />} />
          <Route path="products" element={<ProductsPage />} />

          <Route element={<ProtectedRoute allowedRoles={[ROLES.SUPER_ADMIN, ROLES.ADMIN]} />}>
            <Route path="users" element={<UsersPage />} />
            <Route path="roles" element={<RolesPage />} />
            <Route path="categories" element={<CategoriesPage />} />
            <Route path="suppliers" element={<SuppliersPage />} />
            <Route path="taxes" element={<TaxesPage />} />
          </Route>
        </Route>

        {/* Si se intenta acceder a una ruta no definida, se redirige a la raíz */}
        <Route path="*" element={<Navigate to="/" />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
