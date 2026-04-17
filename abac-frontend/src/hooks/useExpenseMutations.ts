import { useMutation, useQueryClient } from '@tanstack/react-query';
import {
  approveExpense,
  createExpense,
  deleteExpense,
  payExpense,
  rejectExpense,
  submitExpense,
  updateExpense,
} from '../api/expenses';

export function useExpenseMutations() {
  const qc = useQueryClient();
  const invalidate = () => qc.invalidateQueries({ queryKey: ['expenses'] });

  const create = useMutation({
    mutationFn: createExpense,
    onSuccess: invalidate,
  });

  const update = useMutation({
    mutationFn: (args: { id: number; body: { amount: string; description: string } }) =>
      updateExpense(args.id, args.body),
    onSuccess: invalidate,
  });

  const remove = useMutation({
    mutationFn: deleteExpense,
    onSuccess: invalidate,
  });

  const submit = useMutation({
    mutationFn: submitExpense,
    onSuccess: invalidate,
  });

  const approve = useMutation({
    mutationFn: approveExpense,
    onSuccess: invalidate,
  });

  const reject = useMutation({
    mutationFn: rejectExpense,
    onSuccess: invalidate,
  });

  const pay = useMutation({
    mutationFn: payExpense,
    onSuccess: invalidate,
  });

  return { create, update, remove, submit, approve, reject, pay };
}
