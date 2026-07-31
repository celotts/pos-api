import React from 'react';

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  children: React.ReactNode;
  className?: string;
}

export const Button: React.FC<ButtonProps> = ({ children, className = '', ...props }) => {
  const baseClasses = "px-4 py-2 font-semibold rounded-lg shadow-md focus:outline-none focus:ring-2 focus:ring-offset-2";
  
  // Clases por defecto para un botón primario, pero se pueden sobreescribir
  const defaultClasses = "bg-indigo-600 text-white hover:bg-indigo-700 focus:ring-indigo-500";

  return (
    <button
      className={`${baseClasses} ${defaultClasses} ${className}`}
      {...props}
    >
      {children}
    </button>
  );
};

export default Button;
