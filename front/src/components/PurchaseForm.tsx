import React, { useState, useEffect } from 'react';
import { Purchase, PurchaseRequest } from '../models/purchase.model';
import { Supplier, supplierService } from '../services/supplierService';
import { Product, productService } from '../services/productService';
import Button from './ui/Button';

interface PurchaseFormProps {
  onSubmit: (data: PurchaseRequest) => void;
  onCancel: () => void;
  initialData?: Purchase | null;
  isSubmitting: boolean;
  apiError: { message: string; errors?: string[] } | null;
}

const PurchaseForm: React.FC<PurchaseFormProps> = ({ onSubmit, onCancel, initialData, isSubmitting, apiError }) => {
  const [supplierId, setSupplierId] = useState(initialData?.supplierId || '');
  const [purchaseDate, setPurchaseDate] = useState(initialData?.purchaseDate || new Date().toISOString().split('T')[0]);
  const [items, setItems] = useState(initialData?.items || []);

  const [suppliers, setSuppliers] = useState<Supplier[]>([]);
  const [products, setProducts] = useState<Product[]>([]);

  const [newItemProduct, setNewItemProduct] = useState('');
  const [newItemQuantity, setNewItemQuantity] = useState(1);
  const [newItemPrice, setNewItemPrice] = useState(0);

  useEffect(() => {
    supplierService.getAll().then(setSuppliers);
    productService.getAll().then(setProducts);
  }, []);

  const handleAddItem = () => {
    if (!newItemProduct || newItemQuantity <= 0 || newItemPrice <= 0) {
      alert('Please select a product and enter a valid quantity and price.');
      return;
    }
    const product = products.find(p => p.id === newItemProduct);
    if (!product) {
        alert('Product not found');
        return;
    }

    setItems([...items, { productId: product.id, productName: product.name, quantity: newItemQuantity, unitPrice: newItemPrice, subtotal: newItemQuantity * newItemPrice }]);
    setNewItemProduct('');
    setNewItemQuantity(1);
    setNewItemPrice(0);
  };

  const handleRemoveItem = (index: number) => {
    setItems(items.filter((_, i) => i !== index));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!supplierId || items.length === 0) {
      alert('Please select a supplier and add at least one item.');
      return;
    }
    const purchaseRequest: PurchaseRequest = {
      supplierId,
      purchaseDate: new Date(purchaseDate).toISOString(),
      items: items.map(i => ({ productId: i.productId, quantity: i.quantity, unitPrice: i.unitPrice })),
    };
    onSubmit(purchaseRequest);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <label htmlFor="supplier" className="block text-sm font-medium text-gray-700">Supplier</label>
        <select id="supplier" value={supplierId} onChange={e => setSupplierId(e.target.value)} required
          className="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md">
          <option value="">Select a supplier</option>
          {suppliers.map(s => <option key={s.id} value={s.id}>{s.businessName}</option>)}
        </select>
      </div>
      <div>
        <label htmlFor="purchaseDate" className="block text-sm font-medium text-gray-700">Purchase Date</label>
        <input type="date" id="purchaseDate" value={purchaseDate} onChange={e => setPurchaseDate(e.target.value)} required
          className="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md"/>
      </div>

      <div className="space-y-2">
        <h3 className="text-lg font-medium">Items</h3>
        {items.map((item, index) => (
          <div key={index} className="flex items-center justify-between p-2 border rounded-md">
            <div>{products.find(p => p.id === item.productId)?.name} - {item.quantity} @ ${item.unitPrice.toFixed(2)}</div>
            <Button type="button" variant="danger" onClick={() => handleRemoveItem(index)}>Remove</Button>
          </div>
        ))}
      </div>

      <div className="p-4 border-t space-y-3">
        <h4 className="font-semibold">Add New Item</h4>
        <div className="grid grid-cols-3 gap-4">
          <select value={newItemProduct} onChange={e => setNewItemProduct(e.target.value)} className="col-span-2 mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md">
            <option value="">Select a product</option>
            {products.map(p => <option key={p.id} value={p.id}>{p.name}</option>)}
          </select>
          <input type="number" placeholder="Qty" value={newItemQuantity} onChange={e => setNewItemQuantity(Number(e.target.value))} min="1" className="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md"/>
        </div>
        <input type="number" placeholder="Unit Price" value={newItemPrice} onChange={e => setNewItemPrice(Number(e.target.value))} min="0.01" step="0.01" className="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md"/>
        <Button type="button" onClick={handleAddItem}>+ Add Item</Button>
      </div>

      {apiError && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative" role="alert">
          <strong className="font-bold">Error!</strong>
          <span className="block sm:inline"> {apiError.message}</span>
          {apiError.errors && (
            <ul className="list-disc pl-5 mt-2">
              {apiError.errors.map((error, index) => <li key={index}>{error}</li>)}
            </ul>
          )}
        </div>
      )}

      <div className="flex justify-end space-x-2 pt-4">
        <Button type="button" variant="secondary" onClick={onCancel} disabled={isSubmitting}>Cancel</Button>
        <Button type="submit" disabled={isSubmitting}>{isSubmitting ? 'Submitting...' : 'Submit'}</Button>
      </div>
    </form>
  );
};

export default PurchaseForm;
