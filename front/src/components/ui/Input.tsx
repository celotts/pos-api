import React, { InputHTMLAttributes } from 'react';

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  label: string;
  error?: string;
}

const Input: React.FC<InputProps> = ({ label, id, error, ...props }) => {
  const baseClasses = "w-full px-3 py-2 leading-tight text-secondary-700 border rounded shadow-sm appearance-none focus:outline-none focus:ring-2 focus:ring-primary-500";
  const errorClasses = "border-error-500 focus:ring-error-500";
  
  return (
    <div className="mb-4">
      <label className="block text-secondary-700 text-sm font-bold mb-2" htmlFor={id}>
        {label}
      </label>
      <input
        id={id}
        className={`${baseClasses} ${error ? errorClasses : 'border-secondary-300'}`}
        {...props}
      />
      {error && <p className="text-error-500 text-xs mt-1">{error}</p>}
    </div>
  );
};

export default Input;
