import { useSelector } from 'react-redux';
import { selectCurrentUser } from '../store/slices/authSlice';
import { ROLES } from '../utils/roles';

export const useAuth = () => {
  const user = useSelector(selectCurrentUser);

  const hasRole = (roles: string[]): boolean => {
    if (!user) return false;
    return user.roles.some(role => roles.includes(role));
  };

  return {
    user,
    roles: user?.roles || [],
    isSuperAdmin: hasRole([ROLES.SUPER_ADMIN]),
    isAdmin: hasRole([ROLES.SUPER_ADMIN, ROLES.ADMIN]),
    isEditor: hasRole([ROLES.SUPER_ADMIN, ROLES.ADMIN, ROLES.EDITOR]),
    isViewer: hasRole([ROLES.VIEWER]),
  };
};
