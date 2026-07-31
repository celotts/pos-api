import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { AppDispatch } from '../store/store';
import {
  fetchProducts,
  createProduct,
  updateProduct,
  deleteProduct,
  selectAllProducts,
  selectProductsLoading,
  selectProductsError,
} from '../store/slices/productSlice';
import { Product, ProductPayload } from '../models/product.model';
import ProductList from '../components/products/ProductList';
import ProductForm from '../components/products/ProductForm';
import Modal from '../components/ui/Modal';
import Button from '../components/ui/Button';

const ProductsPage: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const products = useSelector(selectAllProducts);
  const loading = useSelector(selectProductsLoading);
  const error = useSelector(selectProductsError);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [productToEdit, setProductToEdit] = useState<Product | null>(null);

  useEffect(() => {
    dispatch(fetchProducts());
  }, [dispatch]);

  const handleAddNew = () => {
    setProductToEdit(null);
    setIsModalOpen(true);
  };

  const handleEdit = (product: Product) => {
    setProductToEdit(product);
    setIsModalOpen(true);
  };

  const handleDelete = (id: string) => {
    if (window.confirm('Are you sure you want to delete this product?')) {
      dispatch(deleteProduct(id));
    }
  };

  const handleSave = (productData: ProductPayload) => {
    if (productToEdit) {
      dispatch(updateProduct({ id: productToEdit.id, product: productData }));
    } else {
      dispatch(createProduct(productData));
    }
    setIsModalOpen(false);
  };

  return (
    <div className="container mx-auto">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold">Product Management</h1>
        <Button onClick={handleAddNew}>
          Add New Product
        </Button>
      </div>

      {loading === 'pending' && <p>Loading...</p>}
      {error && <p className="text-red-500">Error: {error}</p>}

      <ProductList products={products} onEdit={handleEdit} onDelete={handleDelete} />

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)}>
        <h2 className="text-2xl font-bold mb-4">
          {productToEdit ? 'Edit Product' : 'Add New Product'}
        </h2>
        <ProductForm
          productToEdit={productToEdit}
          onSave={handleSave}
          onCancel={() => setIsModalOpen(false)}
        />
      </Modal>
    </div>
  );
};

export default ProductsPage;
