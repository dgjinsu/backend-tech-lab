import { useState } from 'react';
import { AxiosError } from 'axios';
import { Navbar } from '../components/Navbar';
import { ExpenseTable } from '../components/ExpenseTable';
import { ExpenseForm, type ExpenseFormValues } from '../components/ExpenseForm';
import { Modal } from '../components/ui/Modal';
import { Button } from '../components/ui/Button';
import { useAuth } from '../auth/useAuth';
import { useExpenses } from '../hooks/useExpenses';
import { useExpenseMutations } from '../hooks/useExpenseMutations';
import { canDelete, canEditDraft } from '../lib/permissions';
import { WorkflowActions } from '../components/WorkflowActions';
import type { Expense } from '../types/expense';

export function ExpensesPage() {
  const { user } = useAuth();
  const [page, setPage] = useState(0);
  const { data, isLoading, isError, error, refetch } = useExpenses(page);
  const { create, update, remove, submit, approve, reject, pay } = useExpenseMutations();

  const [createOpen, setCreateOpen] = useState(false);
  const [editing, setEditing] = useState<Expense | null>(null);

  if (!user) return null;

  const handleCreate = async (values: ExpenseFormValues) => {
    await create.mutateAsync(values);
    setCreateOpen(false);
  };

  const handleUpdate = async (values: ExpenseFormValues) => {
    if (!editing) return;
    await update.mutateAsync({ id: editing.id, body: values });
    setEditing(null);
  };

  const handleDelete = async (id: number) => {
    if (!confirm(`지출 #${id}를 삭제할까요?`)) return;
    try {
      await remove.mutateAsync(id);
    } catch (err) {
      if (err instanceof AxiosError && err.response) {
        alert(`삭제 실패: HTTP ${err.response.status}`);
      }
    }
  };

  const renderActions = (row: Expense) => (
    <div className="flex flex-wrap justify-end gap-1">
      {canEditDraft(user, row) && (
        <Button size="sm" variant="secondary" onClick={() => setEditing(row)}>
          수정
        </Button>
      )}
      {canDelete(user, row) && (
        <Button size="sm" variant="danger" onClick={() => handleDelete(row.id)}>
          삭제
        </Button>
      )}
      <WorkflowActions
        user={user}
        expense={row}
        submit={submit}
        approve={approve}
        reject={reject}
        pay={pay}
      />
    </div>
  );

  return (
    <div className="min-h-screen">
      <Navbar
        actions={
          <Button onClick={() => setCreateOpen(true)}>+ 새 지출</Button>
        }
      />

      <main className="mx-auto max-w-6xl px-6 py-8">
        <div className="mb-4 flex items-end justify-between">
          <div>
            <h2 className="text-xl font-semibold text-slate-900">지출 내역</h2>
            <p className="mt-1 text-sm text-slate-500">
              서버가 역할별 데이터 스코프(Specification.visibleTo)를 적용한 결과입니다.
            </p>
          </div>
          {data && (
            <p className="text-sm text-slate-500">
              총 {data.totalElements}건 · {data.number + 1} / {Math.max(data.totalPages, 1)}
            </p>
          )}
        </div>

        {isLoading && <p className="text-sm text-slate-500">불러오는 중…</p>}
        {isError && (
          <div className="space-y-2">
            <p className="rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">
              {(error as Error).message}
            </p>
            <Button size="sm" variant="secondary" onClick={() => refetch()}>재시도</Button>
          </div>
        )}

        {data && (
          <ExpenseTable
            rows={data.content}
            highlightOwnerId={user.userId}
            renderActions={renderActions}
          />
        )}

        {data && data.totalPages > 1 && (
          <div className="mt-4 flex justify-end gap-2">
            <Button
              size="sm"
              variant="secondary"
              disabled={data.first}
              onClick={() => setPage((p) => Math.max(0, p - 1))}
            >
              이전
            </Button>
            <Button
              size="sm"
              variant="secondary"
              disabled={data.last}
              onClick={() => setPage((p) => p + 1)}
            >
              다음
            </Button>
          </div>
        )}
      </main>

      <Modal open={createOpen} onClose={() => setCreateOpen(false)} title="새 지출">
        <ExpenseForm submitLabel="생성" onCancel={() => setCreateOpen(false)} onSubmit={handleCreate} />
      </Modal>

      <Modal open={!!editing} onClose={() => setEditing(null)} title={`지출 #${editing?.id ?? ''} 수정`}>
        {editing && (
          <ExpenseForm
            initial={{ amount: String(editing.amount), description: editing.description }}
            submitLabel="저장"
            onCancel={() => setEditing(null)}
            onSubmit={handleUpdate}
          />
        )}
      </Modal>
    </div>
  );
}
