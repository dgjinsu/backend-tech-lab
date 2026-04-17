import { AxiosError } from 'axios';
import type { UseMutationResult } from '@tanstack/react-query';
import type { Expense } from '../types/expense';
import type { AuthUser } from '../types/auth';
import { Button } from './ui/Button';
import { canApprove, canPay, canReject, canSubmit } from '../lib/permissions';

type Mut = UseMutationResult<Expense, Error, number, unknown>;

interface Props {
  user: AuthUser;
  expense: Expense;
  submit: Mut;
  approve: Mut;
  reject: Mut;
  pay: Mut;
}

export function WorkflowActions({ user, expense, submit, approve, reject, pay }: Props) {
  const onAction = async (action: string, mutate: Mut) => {
    try {
      await mutate.mutateAsync(expense.id);
    } catch (err) {
      if (err instanceof AxiosError && err.response) {
        alert(`${action} 실패: HTTP ${err.response.status} — ${err.response.data?.message ?? ''}`);
      }
    }
  };

  return (
    <div className="flex justify-end gap-1">
      {canSubmit(user, expense) && (
        <Button size="sm" variant="secondary" onClick={() => onAction('제출', submit)}>
          제출
        </Button>
      )}
      {canApprove(user, expense) && (
        <Button size="sm" onClick={() => onAction('승인', approve)}>
          승인
        </Button>
      )}
      {canReject(user, expense) && (
        <Button size="sm" variant="danger" onClick={() => onAction('반려', reject)}>
          반려
        </Button>
      )}
      {canPay(user, expense) && (
        <Button size="sm" onClick={() => onAction('지급', pay)}>
          지급
        </Button>
      )}
    </div>
  );
}
