import React, { useState, useMemo, useEffect } from 'react';
import { useDispatch } from 'react-redux';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { setCredentials } from '../store/slices/authSlice';
import { authService } from '../services/authService';

const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

const LoginPage: React.FC = () => {
  const [email, setEmail] = useState('admin@posapi.com');
  const [password, setPassword] = useState('SuperSecretPassword123!');
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const dispatch = useDispatch();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  useEffect(() => {
    if (searchParams.get('sessionExpired')) {
      setError('Your session has expired. Please log in again.');
    }
  }, [searchParams]);

  const isEmailValid = useMemo(() => EMAIL_REGEX.test(email), [email]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    console.log('handleSubmit called!');
    if (!isEmailValid) return;
    setError('');
    setIsSubmitting(true);
    try {
      const { token } = await authService.login({ email, password });
      dispatch(setCredentials({ token }));
      navigate('/');
      console.log('Login successful, navigating to /'); // <-- AÑADIDO
    } catch (err: any) {
      const message = err.response?.data?.message || 'Failed to login. Please check your credentials.';
      setError(message);
      console.error('Login error:', err); // AÑADIDO
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-100">
      <div className="w-full max-w-md rounded-lg bg-white p-8 shadow-lg">
        <h2 className="mb-6 text-center text-2xl font-bold text-gray-800">
          POS System Login
        </h2>
        <form onSubmit={handleSubmit}>
          {error && <p className="mb-4 text-center text-sm text-red-500">{error}</p>}
          <div className="mb-4">
            <label className="mb-2 block text-sm font-bold text-gray-700" htmlFor="email">
              Email
            </label>
            <input
              type="email"
              id="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className={`focus:shadow-outline w-full appearance-none rounded border px-3 py-2 leading-tight text-gray-700 shadow focus:outline-none ${!isEmailValid && email.length > 0 ? 'border-red-500' : ''}`}
              required
            />
            {!isEmailValid && email.length > 0 && (
              <p className="mt-2 text-xs text-red-500">Please enter a valid email format.</p>
            )}
          </div>
          <div className="mb-6">
            <label className="mb-2 block text-sm font-bold text-gray-700" htmlFor="password">
              Password
            </label>
            <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="focus:shadow-outline w-full appearance-none rounded border px-3 py-2 leading-tight text-gray-700 shadow focus:outline-none"
              required
            />
          </div>
          <div className="flex items-center justify-between">
            <button
              type="submit"
              className="focus:shadow-outline w-full rounded bg-blue-500 px-4 py-2 font-bold text-white hover:bg-blue-700 focus:outline-none disabled:opacity-50"
              disabled={!isEmailValid || isSubmitting}
            >
              {isSubmitting ? 'Signing In...' : 'Sign In'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default LoginPage;
