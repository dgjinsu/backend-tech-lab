import client from './client';
import type { ApiResponse, PageResponse } from '../types/common';
import type { Expense, CreateExpenseRequest, UpdateExpenseRequest, GetExpensesParams } from '../types/expense';

export const getExpenses = (params: GetExpensesParams) =>
  client.get<ApiResponse<PageResponse<Expense>>>('/expenses', { params });

export const getExpense = (id: number) =>
  client.get<ApiResponse<Expense>>(`/expenses/${id}`);

export const createExpense = (data: CreateExpenseRequest) =>
  client.post<ApiResponse<Expense>>('/expenses', data);

export const updateExpense = (id: number, data: UpdateExpenseRequest) =>
  client.patch<ApiResponse<Expense>>(`/expenses/${id}`, data);

export const deleteExpense = (id: number) =>
  client.delete<ApiResponse<void>>(`/expenses/${id}`);
