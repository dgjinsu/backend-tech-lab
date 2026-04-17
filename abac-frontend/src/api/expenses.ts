import { client } from './client';
import type { Expense, PageResponse } from '../types/expense';

export async function listExpenses(page = 0, size = 20): Promise<PageResponse<Expense>> {
  const res = await client.get<PageResponse<Expense>>('/expenses', { params: { page, size } });
  return res.data;
}

export async function getExpense(id: number): Promise<Expense> {
  const res = await client.get<Expense>(`/expenses/${id}`);
  return res.data;
}

export async function createExpense(body: { amount: string; description: string }): Promise<Expense> {
  const res = await client.post<Expense>('/expenses', body);
  return res.data;
}

export async function updateExpense(
  id: number,
  body: { amount: string; description: string },
): Promise<Expense> {
  const res = await client.patch<Expense>(`/expenses/${id}`, body);
  return res.data;
}

export async function deleteExpense(id: number): Promise<void> {
  await client.delete(`/expenses/${id}`);
}

export async function submitExpense(id: number): Promise<Expense> {
  const res = await client.post<Expense>(`/expenses/${id}/submit`);
  return res.data;
}

export async function approveExpense(id: number): Promise<Expense> {
  const res = await client.post<Expense>(`/expenses/${id}/approve`);
  return res.data;
}

export async function rejectExpense(id: number): Promise<Expense> {
  const res = await client.post<Expense>(`/expenses/${id}/reject`);
  return res.data;
}

export async function payExpense(id: number): Promise<Expense> {
  const res = await client.post<Expense>(`/expenses/${id}/pay`);
  return res.data;
}
