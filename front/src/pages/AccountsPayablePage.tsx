import React, { useEffect, useState } from 'react';
import CrudPage from '../components/CrudPage';
import { accountsPayableService } from '../services/accountsPayableService';
import AccountsPayableForm from '../components/AccountsPayableForm';
import { AccountsPayable } from '../models/accountsPayable.model';
import { Supplier, supplierService } from '../services/supplierService';

const AccountsPayablePage: React.FC = () => {
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
      render: (item: AccountsPayable) => <span>{getSupplierName(item.supplierId)}</span>,
    },
    {
      header: 'Purchase',
      accessor: 'purchaseId',
    },
    {
      header: 'Due Date',
      accessor: 'dueDate',
      render: (item: AccountsPayable) => <span>{new Date(item.dueDate).toLocaleDateString()}</span>,
    },
    {
      header: 'Original Amount',
      accessor: 'originalAmount',
      render: (item: AccountsPayable) => <span>${item.originalAmount.toFixed(2)}</span>,
    },
    {
      header: 'Outstanding Amount',
      accessor: 'outstandingAmount',
        render: (item: AccountsPayable) => <span>${item.outstandingAmount.toFixed(2)}</span>,
    },
    {
      header: 'Status',
      accessor: 'status',
    },
  ];

  // TODO: Implement markAsPaid action. The CrudPage component needs to be extended
  // or a custom page needs to be created to handle this action.

  return (
    <CrudPage<AccountsPayable>
      title="Accounts Payable"
      service={accountsPayableService}
      columns={columns}
      form={AccountsPayableForm}
    />
  );
};

export default AccountsPayablePage;
