import React from 'react';
import { NavLink } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { logOut } from './store/slices/authSlice';
import { useAuth } from './hooks/useAuth';

const NavItem: React.FC<{ to: string; children: React.ReactNode }> = ({ to, children }) => (
    <NavLink
        to={to}
        className={({ isActive }) =>
            `block px-4 py-2 rounded hover:bg-gray-700 ${isActive ? 'bg-gray-900' : ''}`
        }
    >
        {children}
    </NavLink>
);

const Sidebar: React.FC = () => {
    const dispatch = useDispatch();
    const { user, isAdmin, isEditor } = useAuth();

    return (
        <div className="w-64 bg-gray-800 text-white flex flex-col">
            <div className="p-4 text-2xl font-bold border-b border-gray-700">
                POS-API
            </div>
            <div className="p-4 border-b border-gray-700">
                <div className="font-bold text-lg">{user?.fullName}</div>
                <div className="text-sm text-gray-400">{user?.roles.join(', ')}</div>
            </div>
            <nav className="flex-1 px-2 py-4 space-y-2">
                {/* Opciones para todos los usuarios autenticados */}
                <NavItem to="/products">Productos</NavItem>
                <NavItem to="/purchases">Compras</NavItem>

                {/* Opciones para Editor y Admin */}
                {(isAdmin || isEditor) && (
                    <>
                        <NavItem to="/categories">Categorías</NavItem>
                        <NavItem to="/suppliers">Proveedores</NavItem>
                        <NavItem to="/taxes">Impuestos</NavItem>
                        <NavItem to="/customers">Clientes</NavItem>
                    </>
                )}

                {/* Opciones solo para Admin */}
                {isAdmin && <NavItem to="/users">Usuarios</NavItem>}
                {isAdmin && <NavItem to="/roles">Roles</NavItem>}
            </nav>
            <div className="p-4 border-t border-gray-700">
                <button onClick={() => dispatch(logOut())} className="w-full text-left px-4 py-2 rounded hover:bg-red-700">
                    Cerrar Sesión
                </button>
            </div>
        </div>
    );
};

export default Sidebar;
