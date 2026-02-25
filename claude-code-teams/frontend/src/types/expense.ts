import type { Category } from './category';

export interface Expense {
  id: number;
  amount: number;
  category: Category;
  date: string;
  memo: string;
  createdAt: string;
}

export interface CreateExpenseRequest {
  amount: number;
  categoryId: number;
  date: string;
  memo?: string;
}

export interface UpdateExpenseRequest {
  amount?: number;
  categoryId?: number;
  date?: string;
  memo?: string;
}

export interface GetExpensesParams {
  year: number;
  month: number;
  categoryId?: number;
  page?: number;
  size?: number;
}
