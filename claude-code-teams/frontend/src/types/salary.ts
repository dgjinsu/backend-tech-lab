export interface FixedExpense {
  id: number;
  name: string;
  amount: number;
}

export interface Salary {
  id: number;
  year: number;
  month: number;
  totalAmount: number;
  fixedExpenseTotal: number;
  availableAmount: number;
  fixedExpenses: FixedExpense[];
  memo?: string;
  createdAt: string;
  updatedAt?: string;
}

export interface CreateSalaryRequest {
  year: number;
  month: number;
  totalAmount: number;
  fixedExpenses: { name: string; amount: number }[];
  memo?: string;
}

export interface UpdateSalaryRequest {
  totalAmount: number;
  fixedExpenses: { name: string; amount: number }[];
  memo?: string;
}
