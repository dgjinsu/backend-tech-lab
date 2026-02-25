import client from './client';
import type { ApiResponse } from '../types/common';
import type { Budget, CreateBudgetRequest, UpdateBudgetRequest } from '../types/budget';

export const getBudgets = (year: number, month: number) =>
  client.get<ApiResponse<Budget[]>>('/budgets', { params: { year, month } });

export const createBudget = (data: CreateBudgetRequest) =>
  client.post<ApiResponse<Budget>>('/budgets', data);

export const updateBudget = (id: number, data: UpdateBudgetRequest) =>
  client.patch<ApiResponse<Budget>>(`/budgets/${id}`, data);

export const deleteBudget = (id: number) =>
  client.delete<ApiResponse<void>>(`/budgets/${id}`);
