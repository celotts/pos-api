import React, { useState, useEffect } from 'react';
import CrudPage from '../components/CrudPage';
import { productService } from '../services/productService';
import { categoryService } from '../services/categoryService'; // Asegúrate de tener estos servicios
import { taxService } from '../services/taxService';
import { supplierService } from '../services/supplierService';
import ProductForm from '../components/ProductForm';

const ProductsPage: React.FC = () => {
  const [catalogs, setCatalogs] = useState({ taxes: [], suppliers: [], categories: [] });

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [taxes, suppliers, categories] = await Promise.all([
          taxService.getAll(),
          supplierService.getAll(),
          categoryService.getAll(),
        ]);
        setCatalogs({ taxes, suppliers, categories });
      } catch (error) {
        console.error("Error cargando catálogos:", error);
      }
    };
    fetchData();
  }, []);

  const columns = [
    { header: 'Name', accessor: 'name' as const },
    { header: 'SKU', accessor: 'sku' as const },
    { header: 'Stock', accessor: 'currentStock' as const },
  ];

  // Inyectamos las props obligatorias aquí
  const FormWithData = (props: any) => (
    <ProductForm
      {...props}
      taxes={catalogs.taxes}
      suppliers={catalogs.suppliers}
      categories={catalogs.categories}
    />
  );

  return (
    <CrudPage
      title="Product"
      service={productService}
      columns={columns}
      form={FormWithData} // Usamos el formulario envuelto
    />
  );
};

export default ProductsPage;
