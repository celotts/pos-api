import React from 'react';
import { Outlet, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { logOut, selectCurrentUser } from '../store/slices/authSlice';
import Sidebar from './Sidebar';

const Navbar: React.FC = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const user = useSelector(selectCurrentUser);

  const handleLogout = () => {
    dispatch(logOut());
    navigate('/login');
  };

  return (
    <header className="bg-white shadow-md">
      <div className="mx-auto flex h-16 items-center justify-end px-6">
        <div className="flex items-center space-x-4">
          <span className="text-gray-600">Welcome, {user?.email}</span>
          <button
            onClick={handleLogout}
            className="rounded bg-red-500 px-4 py-2 font-bold text-white hover:bg-red-700"
          >
            Logout
          </button>
        </div>
      </div>
    </header>
  );
};

const MainLayout: React.FC = () => {
  return (
    <div className="flex h-screen bg-gray-100">
      <Sidebar />
      <div className="flex flex-1 flex-col overflow-hidden">
        <Navbar />
        <main className="flex-1 overflow-y-auto p-8">
          <Outlet />
        </main>
      </div>
    </div>
  );
};

export default MainLayout;
