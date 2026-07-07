import React, { useState, useEffect } from 'react';
import { Product, ProductData } from '../services/ProductService';

interface SelectOption {
  id: string;
  name: string;
}

interface ProductFormProps {
  onSubmit: (data: ProductData) => void;
  onCancel: () => void;
  initialData?: Product | null;
  taxes: SelectOption[];
  suppliers: SelectOption[];
  categories: SelectOption[];
}

const ProductForm: React.FC<ProductFormProps> = ({
  onSubmit, onCancel, initialData, taxes = [], suppliers = [], categories = []
}) => {
  const [formData, setFormData] = useState<ProductData>({
    sku: '',
    name: '',
    description: '',
    purchasePrice: 0,
    salePrice: 0,
    currentStock: 0,
    taxId: '',
    supplierId: '',
    categoryId: ''
  });

  useEffect(() => {
    if (initialData) {
      setFormData({
        sku: initialData.sku || '',
        name: initialData.name || '',
        description: initialData.description || '',
        purchasePrice: initialData.purchasePrice || 0,
        salePrice: initialData.salePrice || 0,
        currentStock: initialData.currentStock || 0,
        taxId: initialData.taxId || '',
        supplierId: initialData.supplierId || '',
        categoryId: initialData.categoryId || '',
      });
    } else {
      setFormData({
        sku: '', name: '', description: '', purchasePrice: 0, salePrice: 0,
        currentStock: 0, taxId: '', supplierId: '', categoryId: ''
      });
    }
  }, [initialData]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value, type } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'number' ? (value === '' ? 0 : parseFloat(value)) : value
    }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit(formData);
  };

  const inputStyle = "shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline";
  const selectStyle = "shadow border rounded w-full py-2 px-3 text-gray-700 bg-white leading-tight focus:outline-none focus:shadow-outline";
  const labelStyle = "block text-sm font-bold text-gray-700 mb-2";

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div className="grid grid-cols-2 gap-4">
        <div>
          <label className={labelStyle}>SKU</label>
          <input name="sku" value={formData.sku} onChange={handleChange} required className={inputStyle} />
        </div>
        <div>
          <label className={labelStyle}>Nombre</label>
          <input name="name" value={formData.name} onChange={handleChange} required className={inputStyle} />
        </div>
      </div>

      <div>
        <label className={labelStyle}>Descripción</label>
        <textarea name="description" value={formData.description} onChange={handleChange} className={inputStyle} />
      </div>

      <div className="grid grid-cols-3 gap-4">
        <div>
          <label className={labelStyle}>Precio Compra</label>
          <input type="number" name="purchasePrice" value={formData.purchasePrice} onChange={handleChange} required className={inputStyle} />
        </div>
        <div>
          <label className={labelStyle}>Precio Venta</label>
          <input type="number" name="salePrice" value={formData.salePrice} onChange={handleChange} required className={inputStyle} />
        </div>
        <div>
          <label className={labelStyle}>Stock</label>
          <input type="number" name="currentStock" value={formData.currentStock} onChange={handleChange} required className={inputStyle} />
        </div>
      </div>

      <div className="grid grid-cols-3 gap-4">
        <div>
          <label className={labelStyle}>Impuesto (Tax)</label>
          <select name="taxId" value={formData.taxId} onChange={handleChange} required className={selectStyle}>
            <option value="">Seleccione...</option>
            {taxes.map((t) => <option key={t.id} value={t.id}>{t.name}</option>)}
          </select>
        </div>
        <div>
          <label className={labelStyle}>Proveedor (Supplier)</label>
          <select name="supplierId" value={formData.supplierId} onChange={handleChange} required className={selectStyle}>
            <option value="">Seleccione...</option>
            {suppliers.map((s) => <option key={s.id} value={s.id}>{s.name}</option>)}
          </select>
        </div>
        <div>
          <label className={labelStyle}>Categoría</label>
          <select name="categoryId" value={formData.categoryId} onChange={handleChange} required className={selectStyle}>
            <option value="">Seleccione...</option>
            {categories.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
          </select>
        </div>
      </div>

      <div className="flex justify-end space-x-2 pt-4">
        <button type="button" onClick={onCancel} className="bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded">Cancelar</button>
        <button type="submit" className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">Guardar</button>
      </div>
    </form>
  );
};

export default ProductForm;
