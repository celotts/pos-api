import React from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { logOut, selectCurrentUser } from '../store/slices/authSlice';
import Button from '../components/ui/Button';

interface NavbarProps {
  onMenuButtonClick: () => void;
}

const Navbar: React.FC<NavbarProps> = ({ onMenuButtonClick }) => {
  const dispatch = useDispatch();
  const user = useSelector(selectCurrentUser);

  const handleLogout = () => {
    dispatch(logOut());
    window.location.href = '/login';
  };

  return (
    <header className="bg-white shadow-md">
      <div className="mx-auto flex h-16 items-center justify-between px-6">
        <button onClick={onMenuButtonClick} className="lg:hidden">
            <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16m-7 6h7" />
            </svg>
        </button>
        <div className="flex-grow"></div>
        <div className="flex items-center space-x-4">
          <span className="text-secondary-600 hidden sm:block">Welcome, {user?.fullName || user?.email}</span>
          <Button
            onClick={handleLogout}
            variant="danger"
          >
            Logout
          </Button>
        </div>
      </div>
    </header>
  );
};

export default Navbar;
