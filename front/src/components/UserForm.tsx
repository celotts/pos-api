import React, { useState, useEffect } from 'react';
import type { User, UserData } from '../services/userService';
import type { Role } from '../services/roleService';

interface FormProps {
  onSubmit: (data: UserData) => void;
  onCancel: () => void;
  initialData?: User | null;
  isSubmitting: boolean;
  apiError: { message: string; errors?: string[] } | null;
  roles: Role[];
}

const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

const UserForm: React.FC<FormProps> = ({ onSubmit, onCancel, initialData, isSubmitting, apiError, roles }) => {
  const [formData, setFormData] = useState<UserData>({
    email: '',
    fullName: '',
    password: '',
    roleId: '',
  });

  const [errors, setErrors] = useState<Partial<Record<keyof UserData, string>>>({});

  useEffect(() => {
    if (initialData) {
      setFormData({
        email: initialData.email,
        fullName: initialData.fullName,
        roleId: initialData.role ? initialData.role.id : '',
        password: '',
      });
    } else {
      setFormData({ email: '', fullName: '', password: '', roleId: '' });
    }
    setErrors({});
  }, [initialData]);

  const validateField = (name: keyof UserData, value: string): string | null => {
    switch (name) {
      case 'fullName':
        return !value ? 'El nombre completo es requerido.' : null;
      case 'email':
        if (!value) return 'El email es requerido.';
        if (!EMAIL_REGEX.test(value)) return 'El formato del email no es válido.';
        return null;
      case 'password':
        if (!initialData && !value) return 'La contraseña es requerida para nuevos usuarios.';
        if (value && value.length < 8) return 'La contraseña debe tener al menos 8 caracteres.';
        return null;
      case 'roleId':
        return !value ? 'Debe seleccionar un rol.' : null;
      default:
        return null;
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target as { name: keyof UserData; value: string };
    setFormData(prev => ({ ...prev, [name]: value }));
    const error = validateField(name, value);
    setErrors(prev => ({ ...prev, [name]: error || undefined }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    let validationErrors: Partial<Record<keyof UserData, string>> = {};
    (Object.keys(formData) as Array<keyof UserData>).forEach(key => {
      const error = validateField(key, formData[key] || '');
      if (error) {
        validationErrors[key] = error;
      }
    });

    setErrors(validationErrors);

    if (Object.keys(validationErrors).length === 0 && !isSubmitting) {
      const dataToSend: Partial<UserData> = { ...formData };
      if (initialData && !dataToSend.password) {
        delete dataToSend.password;
      }
      onSubmit(dataToSend as UserData);
    }
  };

  const isSubmitDisabled = isSubmitting || Object.values(errors).some(e => e);

  return (
    <form onSubmit={handleSubmit} className="space-y-2">
      {apiError && (
        <div className="p-3 mb-2 text-sm text-red-700 bg-red-100 rounded-lg">
          <p className="font-bold">{apiError.message}</p>
          {apiError.errors && (
            <ul className="mt-1.5 list-disc list-inside">
              {apiError.errors.map((err, index) => <li key={index}>{err}</li>)}
            </ul>
          )}
        </div>
      )}

      <div>
        <label htmlFor="fullName" className="block text-sm font-bold text-gray-700 mb-1">Nombre Completo</label>
        <input id="fullName" name="fullName" value={formData.fullName} onChange={handleChange} className={`w-full p-2 border rounded ${errors.fullName ? 'border-red-500' : 'border-gray-300'}`} />
        {errors.fullName && <p className="text-xs text-red-500 mt-1">{errors.fullName}</p>}
      </div>
      <div>
        <label htmlFor="email" className="block text-sm font-bold text-gray-700 mb-1">Email</label>
        <input id="email" type="email" name="email" value={formData.email} onChange={handleChange} className={`w-full p-2 border rounded ${errors.email ? 'border-red-500' : 'border-gray-300'}`} />
        {errors.email && <p className="text-xs text-red-500 mt-1">{errors.email}</p>}
      </div>
      <div>
        <label htmlFor="password" className="block text-sm font-bold text-gray-700 mb-1">Contraseña {initialData ? '(Dejar en blanco para no cambiar)' : ''}</label>
        <input id="password" type="password" name="password" value={formData.password || ''} onChange={handleChange} className={`w-full p-2 border rounded ${errors.password ? 'border-red-500' : 'border-gray-300'}`} />
        {errors.password && <p className="text-xs text-red-500 mt-1">{errors.password}</p>}
      </div>
      <div>
        <label htmlFor="roleId" className="block text-sm font-bold text-gray-700 mb-1">Rol</label>
        <select id="roleId" name="roleId" value={formData.roleId} onChange={handleChange} className={`w-full p-2 border rounded ${errors.roleId ? 'border-red-500' : 'border-gray-300'}`}>
          <option value="" disabled>Seleccione un rol</option>
          {roles.map(role => (<option key={role.id} value={role.id}>{role.name}</option>))}
        </select>
        {errors.roleId && <p className="text-xs text-red-500 mt-1">{errors.roleId}</p>}
      </div>

      <div className="flex justify-end space-x-2 pt-4">
        <button type="button" onClick={onCancel} className="bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded" disabled={isSubmitting}>Cancelar</button>
        <button type="submit" className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded disabled:opacity-50" disabled={isSubmitDisabled}>
          {isSubmitting ? 'Guardando...' : 'Guardar'}
        </button>
      </div>
    </form>
  );
};

export default UserForm;
