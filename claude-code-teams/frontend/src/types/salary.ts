export interface Salary {
  id: number;
  amount: number;
  fixedExpense: number;
  year: number;
  month: number;
  memo: string;
  availableAmount: number;
}

export interface CreateSalaryRequest {
  amount: number;
  fixedExpense?: number;
  year: number;
  month: number;
  memo?: string;
}

export interface UpdateSalaryRequest {
  amount?: number;
  fixedExpense?: number;
  memo?: string;
}
