import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { useSelector } from 'react-redux';
import MainLayout from './layouts/MainLayout';
import LoginPage from './pages/LoginPage';
import CategoriesPage from './pages/CategoriesPage';
import RolesPage from './pages/RolesPage';
import SuppliersPage from './pages/SuppliersPage';
import TaxesPage from './pages/TaxesPage';
import { selectIsAuthenticated } from './store/slices/authSlice';
import ProtectedRoute from './components/ProtectedRoute';
import { ROLES } from './utils/roles';

const DashboardPage = () => <h1 className="text-3xl font-bold">Welcome to the Dashboard</h1>;

function App() {
  const isAuthenticated = useSelector(selectIsAuthenticated);

  return (
    <BrowserRouter
      future={{
        v7_startTransition: true,
        v7_relativeSplatPath: true,
      }}
    >
      <Routes>
        {/* Ruta pública: Login */}
        <Route path="/login" element={!isAuthenticated ? <LoginPage /> : <Navigate to="/" />} />

        {/* Rutas Privadas envueltas en el MainLayout */}
        <Route path="/" element={isAuthenticated ? <MainLayout /> : <Navigate to="/login" />}>
          <Route index element={<DashboardPage />} />

          {/* Rutas protegidas por Roles */}
          <Route element={<ProtectedRoute allowedRoles={[ROLES.SUPER_ADMIN, ROLES.ADMIN, ROLES.EDITOR, ROLES.VIEWER]} />}>
            <Route path="categories" element={<CategoriesPage />} />
          </Route>

          <Route element={<ProtectedRoute allowedRoles={[ROLES.SUPER_ADMIN, ROLES.ADMIN]} />}>
            <Route path="roles" element={<RolesPage />} />
            <Route path="suppliers" element={<SuppliersPage />} />
            <Route path="taxes" element={<TaxesPage />} />
          </Route>
        </Route>

        {/* Catch-all para redirigir cualquier ruta inexistente */}
        <Route path="*" element={<Navigate to="/" />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
