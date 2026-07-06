import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { useSelector } from 'react-redux';
import MainLayout from './layouts/MainLayout';
import LoginPage from './pages/LoginPage';
import CategoriesPage from './pages/CategoriesPage';
import RolesPage from './pages/RolesPage'; // Placeholder
import SuppliersPage from './pages/SuppliersPage'; // Placeholder
import { selectIsAuthenticated } from './store/slices/authSlice';
import ProtectedRoute from './components/ProtectedRoute';
import { ROLES } from './utils/roles';

const DashboardPage = () => <h1 className="text-3xl font-bold">Welcome to the Dashboard</h1>;
const PlaceholderPage = ({ title }: { title: string }) => <h1 className="text-3xl font-bold">{title}</h1>;

function App() {
  const isAuthenticated = useSelector(selectIsAuthenticated);

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={isAuthenticated ? <Navigate to="/" /> : <LoginPage />} />

        <Route
          path="/"
          element={isAuthenticated ? <MainLayout /> : <Navigate to="/login" />}
        >
          <Route index element={<DashboardPage />} />

          <Route element={<ProtectedRoute allowedRoles={[ROLES.SUPER_ADMIN, ROLES.ADMIN]} />}>
            <Route path="categories" element={<CategoriesPage />} />
            <Route path="roles" element={<PlaceholderPage title="Roles" />} />
            <Route path="suppliers" element={<PlaceholderPage title="Suppliers" />} />
          </Route>
        </Route>

        <Route path="*" element={<Navigate to="/" />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
