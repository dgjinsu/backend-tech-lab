export interface Expense {
  id: number;
  category: {
    id: number;
    name: string;
    color: string | null;
    icon: string | null;
  };
  amount: number;
  description: string;
  expenseDate: string; // yyyy-MM-dd
  memo: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface CreateExpenseRequest {
  categoryId: number;
  amount: number;
  description: string;
  expenseDate: string; // yyyy-MM-dd
  memo?: string;
}

export interface UpdateExpenseRequest {
  categoryId: number;
  amount: number;
  description: string;
  expenseDate: string;
  memo?: string;
}

export interface GetExpensesParams {
  year: number;
  month: number;
  categoryId?: number;
  page?: number;
  size?: number;
  sort?: string;
}
