import React, { useState, useEffect } from 'react';
import { getProducts } from '../../api/productService'; // Asegúrate que la ruta a tu servicio es correcta
import { Product } from './types'; // Asegúrate que la ruta a tus tipos es correcta

const ProductList: React.FC = () => {
    const [products, setProducts] = useState<Product[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchProducts = async () => {
            try {
                setLoading(true);
                const data = await getProducts();
                setProducts(data);
                setError(null);
            } catch (err) {
                setError('Error al cargar los productos. Asegúrate de que la API esté corriendo y accesible.');
                console.error(err);
            } finally {
                setLoading(false);
            }
        };

        fetchProducts();
    }, []);

    if (loading) {
        return <div>Cargando productos...</div>;
    }

    if (error) {
        return <div style={{ color: 'red' }}>{error}</div>;
    }

    return (
        <div className="container mx-auto">
            <h1 className="text-2xl font-bold mb-4">Gestión de Productos</h1>
            <div className="bg-white shadow-md rounded-lg overflow-hidden">
                <table className="min-w-full leading-normal">
                    <thead>
                        <tr className="bg-gray-200 text-gray-600 uppercase text-sm leading-normal">
                            <th className="py-3 px-6 text-left">SKU</th>
                            <th className="py-3 px-6 text-left">Nombre</th>
                            <th className="py-3 px-6 text-right">Precio Venta</th>
                            <th className="py-3 px-6 text-right">Stock</th>
                            <th className="py-3 px-6 text-center">Acciones</th>
                        </tr>
                    </thead>
                    <tbody className="text-gray-600 text-sm font-light">
                        {products.length > 0 ? (
                            products.map((product) => (
                            <tr key={product.id} className="border-b border-gray-200 hover:bg-gray-100">
                                <td className="py-3 px-6 text-left whitespace-nowrap">{product.sku}</td>
                                <td className="py-3 px-6 text-left">{product.name}</td>
                                <td className="py-3 px-6 text-right">${product.salePrice.toFixed(2)}</td>
                                <td className="py-3 px-6 text-right">{product.currentStock}</td>
                                <td className="py-3 px-6 text-center">
                                    <div className="flex item-center justify-center">
                                        <button className="w-4 mr-2 transform hover:text-purple-500 hover:scale-110">
                                            {/* Icono de Editar */}
                                        </button>
                                        <button className="w-4 mr-2 transform hover:text-red-500 hover:scale-110">
                                            {/* Icono de Eliminar */}
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        ))
                        ) : (
                            <tr><td colSpan={5} className="text-center py-4">No se encontraron productos.</td></tr>
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default ProductList;