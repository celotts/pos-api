import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate, useLocation } from 'react-router-dom';
import { login, selectAuthLoading, selectLoginAttempts, selectIsBlocked } from '../store/slices/authSlice';
import { useAuth } from '../hooks/useAuth';
import toast from 'react-hot-toast';
import Button from '../components/ui/Button';

const LoginPage: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const location = useLocation();
  const { user } = useAuth();

  const isLoading = useSelector(selectAuthLoading);
  const loginAttempts = useSelector(selectLoginAttempts);
  const isBlocked = useSelector(selectIsBlocked);

  const from = location.state?.from?.pathname || '/';

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (isLoading || isBlocked) return;
    setError(null);

    try {
      // .unwrap() lanzará un error si el thunk es rechazado
      await dispatch(login({ email, password, from, navigate })).unwrap();
      toast.success('¡Bienvenido!');
      // La redirección ahora es manejada por el thunk `login`
    } catch (err: any) {
      const remainingAttempts = 3 - (loginAttempts + 1);
      let errorMessage = err.message || 'Error al iniciar sesión. Verifique sus credenciales.';
      if (remainingAttempts > 0) {
        errorMessage += ` Quedan ${remainingAttempts} intentos.`;
      } else {
        errorMessage = 'Usuario bloqueado. Demasiados intentos fallidos.';
      }
      setError(errorMessage);
      toast.error(errorMessage);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-100">
      <div className="w-full max-w-md space-y-8 rounded-xl bg-white p-8 shadow-lg">
        <div>
          {/* Puedes reemplazar esto con tu logo */}
          <h2 className="text-center text-3xl font-bold tracking-tight text-gray-900">
            POS-API
          </h2>
          <p className="mt-2 text-center text-sm text-gray-600">
            Inicia sesión para continuar
          </p>
        </div>

        {isBlocked && (
          <div className="rounded-md bg-red-50 p-4">
            <div className="flex">
              <div className="ml-3">
                <h3 className="text-sm font-medium text-red-800">
                  Usuario bloqueado. Demasiados intentos fallidos.
                </h3>
              </div>
            </div>
          </div>
        )}

        {error && !isLoading && !isBlocked && (
          <div className="rounded-md bg-red-50 p-4">
            <div className="flex">
              <div className="ml-3">
                <h3 className="text-sm font-medium text-red-800">
                  {error}
                </h3>
              </div>
            </div>
          </div>
        )}

        <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
          <div className="space-y-4 rounded-md shadow-sm">
            <div>
              <label htmlFor="email-address" className="sr-only">Email</label>
              <input
                id="email-address"
                name="email"
                type="email"
                autoComplete="email"
                required
                className="relative block w-full appearance-none rounded-md border border-gray-300 px-3 py-2 text-gray-900 placeholder-gray-500 focus:z-10 focus:border-indigo-500 focus:outline-none focus:ring-indigo-500 sm:text-sm"
                placeholder="Correo electrónico"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                disabled={isLoading || isBlocked}
              />
            </div>
            <div>
              <label htmlFor="password" className="sr-only">Contraseña</label>
              <input
                id="password"
                name="password"
                type="password"
                autoComplete="current-password"
                required
                className="relative block w-full appearance-none rounded-md border border-gray-300 px-3 py-2 text-gray-900 placeholder-gray-500 focus:z-10 focus:border-indigo-500 focus:outline-none focus:ring-indigo-500 sm:text-sm"
                placeholder="Contraseña"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                disabled={isLoading || isBlocked}
              />
            </div>
          </div>

          <div>
            <Button type="submit" className="w-full" disabled={isLoading || isBlocked}>
              {isLoading ? 'Cargando...' : 'Ingresar'}
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default LoginPage;