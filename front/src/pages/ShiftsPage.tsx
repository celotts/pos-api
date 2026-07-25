import React from 'react';
import CrudPage from '../components/CrudPage';
import { shiftService } from '../services/shiftService';
import ShiftForm from '../components/ShiftForm';
import { Shift } from '../models/shift.model';

const ShiftsPage: React.FC = () => {

  const columns = [
    {
      header: 'User',
      accessor: 'createdByName',
    },
    {
      header: 'POS Terminal',
      accessor: 'posTerminalId',
    },
    {
      header: 'Start Time',
      accessor: 'startTime',
      render: (item: Shift) => <span>{new Date(item.startTime).toLocaleString()}</span>,
    },
    {
        header: 'End Time',
        accessor: 'endTime',
        render: (item: Shift) => <span>{item.endTime ? new Date(item.endTime).toLocaleString() : '-'}</span>,
    },
    {
      header: 'Starting Cash',
      accessor: 'startingCash',
      render: (item: Shift) => <span>${item.startingCash.toFixed(2)}</span>,
    },
    {
        header: 'Ending Cash',
        accessor: 'endingCash',
        render: (item: Shift) => <span>{item.endingCash ? `$${item.endingCash.toFixed(2)}` : '-'}</span>,
    },
    {
      header: 'Status',
      accessor: 'status',
    },
  ];

  // TODO: Implement close and cancel actions. The CrudPage component needs to be extended
  // or a custom page needs to be created to handle these actions.

  return (
    <CrudPage<Shift>
      title="Shifts"
      service={shiftService}
      columns={columns}
      form={ShiftForm}
    />
  );
};

export default ShiftsPage;
