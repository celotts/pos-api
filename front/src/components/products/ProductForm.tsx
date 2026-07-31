import React, { useState, useEffect } from 'react';
import { Product, ProductPayload } from '../../models/product.model';
import { Button } from '../ui/Button';

interface ProductFormProps {
  productToEdit: Product | null;
  onSave: (product: ProductPayload) => void;
  onCancel: () => void;
}

const ProductForm: React.FC<ProductFormProps> = ({ productToEdit, onSave, onCancel }) => {
  const [formData, setFormData] = useState<ProductPayload>({
    sku: '',
    name: '',
    description: '',
    purchasePrice: 0,
    salePrice: 0,
    currentStock: 0,
    productType: 'FINISHED_PRODUCT',
    categoryId: '', // TODO: Cargar categorías en un select
    supplierId: '', // TODO: Cargar proveedores en un select
    taxId: '',      // TODO: Cargar impuestos en un select
  });

  useEffect(() => {
    if (productToEdit) {
      setFormData({
        sku: productToEdit.sku,
        name: productToEdit.name,
        description: productToEdit.description || '',
        purchasePrice: productToEdit.purchasePrice,
        salePrice: productToEdit.salePrice,
        currentStock: productToEdit.currentStock,
        productType: productToEdit.productType,
        categoryId: productToEdit.categoryId,
        supplierId: productToEdit.supplierId || '',
        taxId: productToEdit.taxId || '',
      });
    } else {
      // Reset form for new product
      setFormData({
        sku: '', name: '', description: '', purchasePrice: 0, salePrice: 0,
        currentStock: 0, productType: 'FINISHED_PRODUCT', categoryId: '',
        supplierId: '', taxId: ''
      });
    }
  }, [productToEdit]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSave(formData);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label htmlFor="name" className="block text-sm font-medium text-gray-700">Name</label>
          <input type="text" id="name" name="name" value={formData.name} onChange={handleChange} required
            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"/>
        </div>
        <div>
          <label htmlFor="sku" className="block text-sm font-medium text-gray-700">SKU</label>
          <input type="text" id="sku" name="sku" value={formData.sku} onChange={handleChange} required
            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"/>
        </div>
        <div>
          <label htmlFor="salePrice" className="block text-sm font-medium text-gray-700">Sale Price</label>
          <input type="number" step="0.01" id="salePrice" name="salePrice" value={formData.salePrice} onChange={handleChange} required
            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"/>
        </div>
        <div>
          <label htmlFor="purchasePrice" className="block text-sm font-medium text-gray-700">Purchase Price</label>
          <input type="number" step="0.01" id="purchasePrice" name="purchasePrice" value={formData.purchasePrice} onChange={handleChange} required
            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"/>
        </div>
        <div>
          <label htmlFor="currentStock" className="block text-sm font-medium text-gray-700">Current Stock</label>
          <input type="number" id="currentStock" name="currentStock" value={formData.currentStock} onChange={handleChange} required
            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"/>
        </div>
        <div>
          <label htmlFor="productType" className="block text-sm font-medium text-gray-700">Product Type</label>
          <select id="productType" name="productType" value={formData.productType} onChange={handleChange}
            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500">
            <option value="FINISHED_PRODUCT">Finished Product</option>
            <option value="RAW_MATERIAL">Raw Material</option>
          </select>
        </div>
      </div>
      <div>
        <label htmlFor="description" className="block text-sm font-medium text-gray-700">Description</label>
        <textarea id="description" name="description" value={formData.description} onChange={handleChange} rows={3}
          className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"/>
      </div>
      <div className="flex justify-end space-x-4 pt-4">
        <Button type="button" onClick={onCancel} className="bg-gray-200 text-gray-700 hover:bg-gray-300">
          Cancel
        </Button>
        <Button type="submit">
          Save Product
        </Button>
      </div>
    </form>
  );
};

export default ProductForm;
