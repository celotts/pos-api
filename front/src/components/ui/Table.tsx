import React from 'react';

interface Column<T> {
  header: string;
  accessor: keyof T;
}

interface TableProps<T> {
  columns: Column<T>[];
  data: T[];
  renderActions?: (item: T) => React.ReactNode;
}

const Table = <T extends { id: string }>({ columns, data, renderActions }: TableProps<T>) => {
  return (
    <div className="bg-white shadow-md rounded-lg overflow-x-auto">
      <table className="min-w-full leading-normal">
        <thead>
          <tr>
            {columns.map((col) => (
              <th key={String(col.accessor)} className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                {col.header}
              </th>
            ))}
            {renderActions && <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100"></th>}
          </tr>
        </thead>
        <tbody>
          {data.map((item) => (
            <tr key={item.id}>
              {columns.map((col) => (
                <td key={String(col.accessor)} className="px-5 py-5 border-b border-gray-200 bg-white text-sm">
                  {String(item[col.accessor] ?? 'N/A')}
                </td>
              ))}
              {renderActions && (
                <td className="px-5 py-5 border-b border-gray-200 bg-white text-sm text-right">
                  {renderActions(item)}
                </td>
              )}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default Table;
