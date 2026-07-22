import React from 'react';
import { NavLink } from 'react-router-dom'; // CORREGIDO: Quitado el '1'
import { useDispatch } from 'react-redux';
import { logout } from './authSlice'; // Ruta relativa directa

const Sidebar: React.FC = () => {
    const dispatch = useDispatch();

    return (
        <div className="w-64 bg-gray-800 text-white flex flex-col">
            <div className="p-4 text-2xl font-bold border-b border-gray-700">
                POS-API
            </div>
            <nav className="flex-1 px-2 py-4 space-y-2">
                <NavLink to="/products" className={({ isActive }) => `block px-4 py-2 rounded hover:bg-gray-700 ${isActive ? 'bg-gray-900' : ''}`}>
                    Productos
                </NavLink>
                {/* Aquí puedes agregar más enlaces a futuro (Categorías, Ventas, etc.) */}
            </nav>
            <div className="p-4 border-t border-gray-700">
                <button onClick={() => dispatch(logout())} className="w-full text-left px-4 py-2 rounded hover:bg-red-700">
                    Cerrar Sesión
                </button>
            </div>
        </div>
    );
};

export default Sidebar;
