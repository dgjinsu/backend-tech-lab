import { useQuery } from '@tanstack/react-query';
import { listExpenses } from '../api/expenses';

export function useExpenses(page = 0) {
  return useQuery({
    queryKey: ['expenses', page],
    queryFn: () => listExpenses(page),
  });
}
