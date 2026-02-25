export interface MonthlyStatistics {
  year: number;
  month: number;
  totalIncome: number;
  totalFixedExpense: number;
  totalExpense: number;
  availableAmount: number;
  expenseRate: number;
}

export interface CategoryStatistics {
  categoryId: number;
  categoryName: string;
  amount: number;
  percentage: number;
  budgetAmount: number;
  budgetUsageRate: number;
}
