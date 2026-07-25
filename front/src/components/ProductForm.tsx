import React, { useState, useEffect } from 'react';
import type { ProductData } from '../services/productService';
import { categoryService, Category } from '../services/categoryService';
import { taxService, Tax } from '../services/taxService';
import { supplierService, Supplier } from '../services/supplierService';
import Button from './ui/Button';

// La interfaz de props se alinea con la que CrudPage espera
interface ProductFormProps {
  onSubmit: (data: ProductData) => void;
  onCancel: () => void;
  initialData?: Product | null; // Acepta Product, no solo ProductData
  isSubmitting: boolean;
  apiError: { message: string; errors?: string[] } | null;
}

const ProductForm: React.FC<ProductFormProps> = ({ onSubmit, onCancel, initialData, isSubmitting, apiError }) => {
  const [formData, setFormData] = useState<ProductData>({
    sku: '',
    name: '',
    description: '',
    purchasePrice: 0,
    salePrice: 0,
    currentStock: 0,
    categoryId: '',
    taxId: '',
    supplierId: '',
  });

  // Estado para las listas de los selectores
  const [categories, setCategories] = useState<Category[]>([]);
  const [taxes, setTaxes] = useState<Tax[]>([]);
  const [suppliers, setSuppliers] = useState<Supplier[]>([]);
  const [loading, setLoading] = useState(true);
  const [fetchError, setFetchError] = useState<string | null>(null);

  // Efecto para popular el formulario con datos iniciales
  useEffect(() => {
    if (initialData) {
      // Mapeamos solo los campos que necesita ProductData
      setFormData({
        sku: initialData.sku,
        name: initialData.name,
        description: initialData.description || '',
        purchasePrice: initialData.purchasePrice,
        salePrice: initialData.salePrice,
        currentStock: initialData.currentStock,
        categoryId: initialData.categoryId,
        taxId: initialData.taxId || '',
        supplierId: initialData.supplierId || '',
      });
    }
  }, [initialData]);

  // Efecto para cargar los datos de los selectores
  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const [categoriesData, taxesData, suppliersData] = await Promise.all([
          categoryService.getAll(),
          taxService.getAll(),
          supplierService.getAll(),
        ]);
        setCategories(categoriesData);
        setTaxes(taxesData);
        setSuppliers(suppliersData);
        setFetchError(null);
      } catch (error) {
        console.error('Failed to fetch data for selectors', error);
        setFetchError('Error al cargar datos para el formulario. Por favor, intente de nuevo.');
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => {
    const { name, value, type } = e.target;
    // Asegurarnos de que los números se guarden como números
    const isNumeric = type === 'number';
    setFormData(prevData => ({
        ...prevData,
        [name]: isNumeric ? parseFloat(value) || 0 : value 
    }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit(formData);
  };

  if (loading) {
    return <div>Cargando formulario...</div>;
  }

  if (fetchError) {
    return <div className="text-red-500">{fetchError}</div>;
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      {/* Muestra de errores de la API */}
      {apiError && (
        <div className="rounded-md bg-red-50 p-4">
          <h3 className="text-sm font-medium text-red-800">{apiError.message}</h3>
          {apiError.errors && (
            <div className="mt-2 text-sm text-red-700">
              <ul className="list-disc space-y-1 pl-5">
                {apiError.errors.map((error, index) => (
                  <li key={index}>{error}</li>
                ))}
              </ul>
            </div>
          )}
        </div>
      )}

      {/* SKU, Name, Description */}
      <input name="sku" value={formData.sku} onChange={handleChange} placeholder="SKU" required className="border p-2 w-full" disabled={isSubmitting} />
      <input name="name" value={formData.name} onChange={handleChange} placeholder="Product Name" required className="border p-2 w-full" disabled={isSubmitting} />
      <textarea name="description" value={formData.description} onChange={handleChange} placeholder="Description" className="border p-2 w-full" disabled={isSubmitting} />

      {/* Prices and Stock */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <input type="number" name="purchasePrice" value={formData.purchasePrice} onChange={handleChange} placeholder="Purchase Price" required className="border p-2 w-full" step="0.01" disabled={isSubmitting} />
        <input type="number" name="salePrice" value={formData.salePrice} onChange={handleChange} placeholder="Sale Price" required className="border p-2 w-full" step="0.01" disabled={isSubmitting} />
        <input type="number" name="currentStock" value={formData.currentStock} onChange={handleChange} placeholder="Stock" required className="border p-2 w-full" disabled={isSubmitting} />
      </div>

      {/* Foreign Keys (Ahora son selectores) */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <select name="categoryId" value={formData.categoryId} onChange={handleChange} required className="border p-2 w-full" disabled={isSubmitting}>
          <option value="">Select Category</option>
          {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
        </select>
        <select name="taxId" value={formData.taxId} onChange={handleChange} className="border p-2 w-full" disabled={isSubmitting}>
          <option value="">Select Tax (Optional)</option>
          {taxes.map(t => <option key={t.id} value={t.id}>{t.name} ({t.percentage}%)</option>)}
        </select>
        <select name="supplierId" value={formData.supplierId} onChange={handleChange} className="border p-2 w-full" disabled={isSubmitting}>
          <option value="">Select Supplier (Optional)</option>
          {suppliers.map(s => <option key={s.id} value={s.id}>{s.businessName}</option>)}
        </select>
      </div>

      <div className="flex justify-end space-x-4 pt-4">
        <Button type="button" onClick={onCancel} variant="secondary" disabled={isSubmitting}>
          Cancel
        </Button>
        <Button type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Saving...' : 'Save'}
        </Button>
      </div>
    </form>
  );
};

export default ProductForm;
