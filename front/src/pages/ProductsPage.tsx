import React from 'react';
import CrudPage from '../components/CrudPage';
import { productService } from '../services/productService';
import ProductForm from '../components/ProductForm';

const ProductsPage: React.FC = () => {
  const columns = [
    { header: 'SKU', accessor: 'sku' as const },
    { header: 'Name', accessor: 'name' as const },
    { header: 'Sale Price', accessor: 'salePrice' as const },
    { header: 'Stock', accessor: 'currentStock' as const },
  ];

  return (
    <CrudPage
      title="Products"
      service={productService}
      columns={columns}
      form={ProductForm}
    />
  );
};

export default ProductsPage;
