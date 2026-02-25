import type { Category } from './category';

export interface Budget {
  id: number;
  category: Category;
  amount: number;
  year: number;
  month: number;
}

export interface CreateBudgetRequest {
  categoryId: number;
  amount: number;
  year: number;
  month: number;
}

export interface UpdateBudgetRequest {
  amount: number;
}
