import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { RootState } from '../store';
import { Customer, CustomerPayload } from '../../models/customer.model';
import * as customerService from '../../services/customer.service';

interface CustomerState {
  customers: Customer[];
  loading: 'idle' | 'pending' | 'succeeded' | 'failed';
  error: string | null;
}

export const fetchCustomers = createAsyncThunk('customers/fetchCustomers', async () => {
  return await customerService.fetchCustomers();
});

export const createCustomer = createAsyncThunk('customers/createCustomer', async (customer: CustomerPayload) => {
  return await customerService.createCustomer(customer);
});

export const updateCustomer = createAsyncThunk('customers/updateCustomer', async ({ id, customer }: { id: string; customer: CustomerPayload }) => {
  return await customerService.updateCustomer(id, customer);
});

export const deleteCustomer = createAsyncThunk('customers/deleteCustomer', async (id: string) => {
  await customerService.deleteCustomer(id);
  return id;
});

const initialState: CustomerState = {
  customers: [],
  loading: 'idle',
  error: null,
};

const customerSlice = createSlice({
  name: 'customers',
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchCustomers.pending, (state) => { state.loading = 'pending'; })
      .addCase(fetchCustomers.fulfilled, (state, action: PayloadAction<Customer[]>) => {
        state.loading = 'succeeded';
        state.customers = action.payload;
      })
      .addCase(fetchCustomers.rejected, (state, action) => {
        state.loading = 'failed';
        state.error = action.error.message || 'Failed to fetch customers';
      })
      .addCase(createCustomer.fulfilled, (state, action: PayloadAction<Customer>) => {
        state.customers.push(action.payload);
      })
      .addCase(updateCustomer.fulfilled, (state, action: PayloadAction<Customer>) => {
        const index = state.customers.findIndex((c) => c.id === action.payload.id);
        if (index !== -1) state.customers[index] = action.payload;
      })
      .addCase(deleteCustomer.fulfilled, (state, action: PayloadAction<string>) => {
        state.customers = state.customers.filter((c) => c.id !== action.payload);
      });
  },
});

export default customerSlice.reducer;

export const selectAllCustomers = (state: RootState) => state.customers.customers;
export const selectCustomersLoading = (state: RootState) => state.customers.loading;
export const selectCustomersError = (state: RootState) => state.customers.error;
