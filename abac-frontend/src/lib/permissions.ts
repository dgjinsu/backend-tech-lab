// 백엔드 ExpensePolicy.java와 동일한 규칙을 TS로 복제.
// UI 힌트일 뿐이며, 실제 권한 판정은 서버가 단독으로 한다.
// 버튼을 숨겨도 수동 호출은 서버가 403/409로 최종 거절.

import type { AuthUser } from '../types/auth';
import type { Expense } from '../types/expense';

export const MANAGER_APPROVAL_LIMIT = 1_000_000;

export function canEditDraft(user: AuthUser, e: Expense): boolean {
  if (user.role === 'ADMIN') return true;
  return e.ownerId === user.userId && e.status === 'DRAFT';
}

export function canDelete(user: AuthUser, e: Expense): boolean {
  return canEditDraft(user, e);
}

export function canSubmit(user: AuthUser, e: Expense): boolean {
  if (user.role === 'ADMIN') return true;
  return e.ownerId === user.userId && e.status === 'DRAFT';
}

export function canApprove(user: AuthUser, e: Expense): boolean {
  if (user.role === 'ADMIN') return true;
  if (user.role !== 'MANAGER') return false;
  return (
    e.departmentId === user.departmentId &&
    e.status === 'SUBMITTED' &&
    Number(e.amount) <= MANAGER_APPROVAL_LIMIT
  );
}

export function canReject(user: AuthUser, e: Expense): boolean {
  return canApprove(user, e);
}

export function canPay(user: AuthUser, e: Expense): boolean {
  if (user.role === 'ADMIN') return true;
  if (user.role !== 'FINANCE') return false;
  return e.status === 'APPROVED';
}
