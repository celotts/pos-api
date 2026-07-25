import React from 'react';
import CrudPage from '../components/CrudPage';
import { cashAccountService } from '../services/cashAccountService';
import CashAccountForm from '../components/CashAccountForm';
import { CashAccount } from '../models/cashAccount.model';

const CashAccountsPage: React.FC = () => {
  const columns = [
    {
      header: 'Name',
      accessor: 'name',
    },
    {
      header: 'Account Type',
      accessor: 'accountType',
    },
    {
      header: 'Current Balance',
      accessor: 'currentBalance',
      render: (item: CashAccount) => <span>${item.currentBalance.toFixed(2)}</span>,
    },
    {
      header: 'Currency',
      accessor: 'currency',
    },
  ];

  return (
    <CrudPage<CashAccount>
      title="Cash Accounts"
      service={cashAccountService}
      columns={columns}
      form={CashAccountForm}
    />
  );
};

export default CashAccountsPage;
