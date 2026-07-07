import React, { useState, useEffect } from 'react';
import { ProductData } from '../services/productService';

interface ProductFormProps {
  onSubmit: (data: ProductData) => void;
  onCancel: () => void;
  initialData?: ProductData | null;
}

const ProductForm: React.FC<ProductFormProps> = ({ onSubmit, onCancel, initialData }) => {
  const [formData, setFormData] = useState<ProductData>({
    sku: '', name: '', description: '', purchasePrice: 0, salePrice: 0, currentStock: 0, categoryId: '', taxId: '', supplierId: ''
  });

  useEffect(() => {
    if (initialData) setFormData(initialData);
  }, [initialData]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit(formData);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      {/* SKU, Name, Description */}
      <input name="sku" value={formData.sku} onChange={handleChange} placeholder="SKU" required className="border p-2 w-full" />
      <input name="name" value={formData.name} onChange={handleChange} placeholder="Product Name" required className="border p-2 w-full" />
      <textarea name="description" value={formData.description} onChange={handleChange} placeholder="Description" className="border p-2 w-full" />

      {/* Prices and Stock */}
      <div className="grid grid-cols-3 gap-4">
        <input type="number" name="purchasePrice" value={formData.purchasePrice} onChange={handleChange} placeholder="Purchase Price" required className="border p-2 w-full" />
        <input type="number" name="salePrice" value={formData.salePrice} onChange={handleChange} placeholder="Sale Price" required className="border p-2 w-full" />
        <input type="number" name="currentStock" value={formData.currentStock} onChange={handleChange} placeholder="Stock" required className="border p-2 w-full" />
      </div>

      {/* Foreign Keys (deberían ser selectores en una app real) */}
      <input name="categoryId" value={formData.categoryId} onChange={handleChange} placeholder="Category ID" required className="border p-2 w-full" />
      <input name="taxId" value={formData.taxId} onChange={handleChange} placeholder="Tax ID" className="border p-2 w-full" />
      <input name="supplierId" value={formData.supplierId} onChange={handleChange} placeholder="Supplier ID" className="border p-2 w-full" />

      <div className="flex justify-end space-x-2 pt-4">
        <button type="button" onClick={onCancel} className="bg-gray-500 text-white p-2 rounded">Cancelar</button>
        <button type="submit" className="bg-blue-500 text-white p-2 rounded">Guardar</button>
      </div>
    </form>
  );
};

export default ProductForm;
