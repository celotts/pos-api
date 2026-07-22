import React, { useEffect, useState } from 'react';
import CrudPage from '../components/CrudPage';
import { purchaseService } from '../services/purchaseService';
import PurchaseForm from '../components/PurchaseForm';
import { Purchase } from '../models/purchase.model';
import { Supplier, supplierService } from '../services/supplierService';

const PurchasesPage: React.FC = () => {
  const [suppliers, setSuppliers] = useState<Supplier[]>([]);

  useEffect(() => {
    // TODO: This is not efficient. The backend should provide the supplier name.
    supplierService.getAll().then(setSuppliers);
  }, []);

  const getSupplierName = (supplierId: string) => {
    return suppliers.find(s => s.id === supplierId)?.businessName || supplierId;
  };

  const columns = [
    {
      header: 'Supplier',
      accessor: 'supplierId',
      render: (item: Purchase) => <span>{getSupplierName(item.supplierId)}</span>,
    },
    {
      header: 'Date',
      accessor: 'purchaseDate',
      render: (item: Purchase) => <span>{new Date(item.purchaseDate).toLocaleDateString()}</span>,
    },
    {
      header: 'Total',
      accessor: 'totalAmount',
      render: (item: Purchase) => <span>${item.totalAmount.toFixed(2)}</span>,
    },
    {
      header: 'Status',
      accessor: 'status',
    },
    {
      header: 'Payment Status',
      accessor: 'paymentStatus',
    },
  ];

  return (
    <CrudPage<Purchase>
      title="Purchases"
      service={purchaseService}
      columns={columns}
      form={PurchaseForm}
    />
  );
};

export default PurchasesPage;
