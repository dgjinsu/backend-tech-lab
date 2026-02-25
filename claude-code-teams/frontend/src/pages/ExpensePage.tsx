import { useState, useMemo } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { getExpenses, createExpense, updateExpense, deleteExpense } from '../api/expense';
import { getCategories } from '../api/category';
import type { Expense, CreateExpenseRequest, UpdateExpenseRequest, GetExpensesParams } from '../types/expense';
import type { Category } from '../types/category';
import { formatCurrency, formatDate } from '../utils/format';
import Card from '../components/common/Card';
import Button from '../components/common/Button';
import Input from '../components/common/Input';
import Modal from '../components/common/Modal';
import Loading from '../components/common/Loading';

type SortOption = 'expenseDate,desc' | 'expenseDate,asc' | 'amount,desc' | 'amount,asc';

const SORT_OPTIONS: { value: SortOption; label: string }[] = [
  { value: 'expenseDate,desc', label: '최신순' },
  { value: 'expenseDate,asc', label: '오래된순' },
  { value: 'amount,desc', label: '금액 높은순' },
  { value: 'amount,asc', label: '금액 낮은순' },
];

const PAGE_SIZE = 20;

export default function ExpensePage() {
  const now = new Date();
  const [selectedYear, setSelectedYear] = useState(now.getFullYear());
  const [selectedMonth, setSelectedMonth] = useState(now.getMonth() + 1);
  const [selectedCategoryId, setSelectedCategoryId] = useState<number | undefined>(undefined);
  const [sort, setSort] = useState<SortOption>('expenseDate,desc');
  const [page, setPage] = useState(0);

  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [editingExpense, setEditingExpense] = useState<Expense | null>(null);
  const [deleteTarget, setDeleteTarget] = useState<Expense | null>(null);

  const queryClient = useQueryClient();

  const params: GetExpensesParams = {
    year: selectedYear,
    month: selectedMonth,
    categoryId: selectedCategoryId,
    page,
    size: PAGE_SIZE,
    sort,
  };

  const { data: expensePage, isLoading } = useQuery({
    queryKey: ['expenses', params],
    queryFn: () => getExpenses(params).then((res) => res.data.data),
  });

  const { data: categories } = useQuery({
    queryKey: ['categories'],
    queryFn: () => getCategories().then((res) => res.data.data),
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => deleteExpense(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['expenses'] });
      setDeleteTarget(null);
    },
  });

  // Calculate total for the current page results
  const totalAmount = useMemo(() => {
    if (!expensePage?.content) return 0;
    return expensePage.content.reduce((sum, e) => sum + e.amount, 0);
  }, [expensePage]);

  // Group expenses by date
  const groupedExpenses = useMemo(() => {
    if (!expensePage?.content) return [];
    const groups: { date: string; expenses: Expense[] }[] = [];
    const map = new Map<string, Expense[]>();
    for (const expense of expensePage.content) {
      const date = expense.expenseDate;
      if (!map.has(date)) {
        map.set(date, []);
      }
      map.get(date)!.push(expense);
    }
    for (const [date, expenses] of map) {
      groups.push({ date, expenses });
    }
    // Sort groups by date descending
    groups.sort((a, b) => b.date.localeCompare(a.date));
    return groups;
  }, [expensePage]);

  const handlePrevMonth = () => {
    if (selectedMonth === 1) {
      setSelectedYear((y) => y - 1);
      setSelectedMonth(12);
    } else {
      setSelectedMonth((m) => m - 1);
    }
    setPage(0);
  };

  const handleNextMonth = () => {
    if (selectedMonth === 12) {
      setSelectedYear((y) => y + 1);
      setSelectedMonth(1);
    } else {
      setSelectedMonth((m) => m + 1);
    }
    setPage(0);
  };

  return (
    <div>
      {/* Header */}
      <div className="mb-6 flex items-center justify-between">
        <h2 className="text-2xl font-bold text-gray-900">지출 관리</h2>
        <Button onClick={() => setIsCreateModalOpen(true)}>+ 지출 등록</Button>
      </div>

      {/* Total Summary */}
      <Card className="mb-6">
        <div className="text-center">
          <p className="text-sm text-gray-500">
            {selectedYear}년 {selectedMonth}월 총 지출
          </p>
          <p className="mt-1 text-3xl font-bold text-red-600">
            {formatCurrency(totalAmount)}
          </p>
          {expensePage && (
            <p className="mt-1 text-xs text-gray-400">
              총 {expensePage.totalElements}건
            </p>
          )}
        </div>
      </Card>

      {/* Filter Bar */}
      <div className="mb-6 flex flex-wrap items-center gap-4">
        {/* Month Navigation */}
        <div className="flex items-center gap-2">
          <Button variant="secondary" size="sm" onClick={handlePrevMonth}>
            &larr;
          </Button>
          <span className="min-w-[120px] text-center text-lg font-semibold text-gray-800">
            {selectedYear}년 {selectedMonth}월
          </span>
          <Button variant="secondary" size="sm" onClick={handleNextMonth}>
            &rarr;
          </Button>
        </div>

        {/* Category Filter */}
        <select
          value={selectedCategoryId ?? ''}
          onChange={(e) => {
            setSelectedCategoryId(e.target.value ? Number(e.target.value) : undefined);
            setPage(0);
          }}
          className="rounded-lg border border-gray-300 px-3 py-2 text-sm text-gray-900 focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500"
        >
          <option value="">전체 카테고리</option>
          {categories?.map((cat) => (
            <option key={cat.id} value={cat.id}>
              {cat.name}
            </option>
          ))}
        </select>

        {/* Sort */}
        <select
          value={sort}
          onChange={(e) => {
            setSort(e.target.value as SortOption);
            setPage(0);
          }}
          className="rounded-lg border border-gray-300 px-3 py-2 text-sm text-gray-900 focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500"
        >
          {SORT_OPTIONS.map((opt) => (
            <option key={opt.value} value={opt.value}>
              {opt.label}
            </option>
          ))}
        </select>
      </div>

      {/* Expense List */}
      {isLoading ? (
        <Loading />
      ) : !expensePage || expensePage.content.length === 0 ? (
        <Card>
          <p className="py-12 text-center text-gray-400">
            {selectedYear}년 {selectedMonth}월 등록된 지출이 없습니다.
          </p>
        </Card>
      ) : (
        <div className="space-y-6">
          {groupedExpenses.map((group) => (
            <div key={group.date}>
              <h3 className="mb-3 text-sm font-semibold text-gray-500">
                {formatDate(group.date)}
              </h3>
              <div className="space-y-2">
                {group.expenses.map((expense) => (
                  <Card
                    key={expense.id}
                    className="cursor-pointer transition-shadow hover:shadow-md"
                  >
                    <div
                      className="flex items-center justify-between"
                      onClick={() => setEditingExpense(expense)}
                    >
                      <div className="flex items-center gap-3">
                        {/* Category Tag */}
                        <span
                          className={
                            expense.category.color
                              ? 'inline-block rounded-full px-3 py-1 text-xs font-medium'
                              : 'inline-block rounded-full bg-gray-100 px-3 py-1 text-xs font-medium text-gray-600'
                          }
                          style={
                            expense.category.color
                              ? {
                                  backgroundColor: `${expense.category.color}20`,
                                  color: expense.category.color,
                                }
                              : undefined
                          }
                        >
                          {expense.category.name}
                        </span>
                        <div>
                          <p className="font-medium text-gray-900">{expense.description}</p>
                          {expense.memo && (
                            <p className="text-xs text-gray-400">{expense.memo}</p>
                          )}
                        </div>
                      </div>
                      <div className="flex items-center gap-3">
                        <span className="text-lg font-bold text-red-600">
                          -{formatCurrency(expense.amount)}
                        </span>
                        <Button
                          variant="danger"
                          size="sm"
                          onClick={(e) => {
                            e.stopPropagation();
                            setDeleteTarget(expense);
                          }}
                        >
                          삭제
                        </Button>
                      </div>
                    </div>
                  </Card>
                ))}
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Pagination */}
      {expensePage && expensePage.totalPages > 1 && (
        <div className="mt-6 flex items-center justify-center gap-2">
          <Button
            variant="secondary"
            size="sm"
            disabled={expensePage.number === 0}
            onClick={() => setPage((p) => p - 1)}
          >
            이전
          </Button>
          {Array.from({ length: expensePage.totalPages }, (_, i) => (
            <Button
              key={i}
              variant={i === expensePage.number ? 'primary' : 'secondary'}
              size="sm"
              onClick={() => setPage(i)}
            >
              {i + 1}
            </Button>
          ))}
          <Button
            variant="secondary"
            size="sm"
            disabled={expensePage.number === expensePage.totalPages - 1}
            onClick={() => setPage((p) => p + 1)}
          >
            다음
          </Button>
        </div>
      )}

      {/* Create Modal */}
      <ExpenseFormModal
        isOpen={isCreateModalOpen}
        onClose={() => setIsCreateModalOpen(false)}
        categories={categories ?? []}
      />

      {/* Edit Modal */}
      {editingExpense && (
        <ExpenseFormModal
          isOpen={true}
          onClose={() => setEditingExpense(null)}
          categories={categories ?? []}
          expense={editingExpense}
        />
      )}

      {/* Delete Confirmation */}
      <Modal
        isOpen={deleteTarget !== null}
        onClose={() => setDeleteTarget(null)}
        title="지출 삭제"
      >
        <p className="mb-4 text-gray-600">
          &quot;{deleteTarget?.description}&quot; ({deleteTarget ? formatCurrency(deleteTarget.amount) : ''})을
          삭제하시겠습니까?
        </p>
        <div className="flex justify-end gap-2">
          <Button variant="secondary" onClick={() => setDeleteTarget(null)}>
            취소
          </Button>
          <Button
            variant="danger"
            disabled={deleteMutation.isPending}
            onClick={() => deleteTarget && deleteMutation.mutate(deleteTarget.id)}
          >
            {deleteMutation.isPending ? '삭제 중...' : '삭제'}
          </Button>
        </div>
      </Modal>
    </div>
  );
}

// ─── Expense Form Modal ──────────────────────────────────────

interface ExpenseFormModalProps {
  isOpen: boolean;
  onClose: () => void;
  categories: Category[];
  expense?: Expense;
}

function ExpenseFormModal({ isOpen, onClose, categories, expense }: ExpenseFormModalProps) {
  const isEdit = !!expense;

  const [categoryId, setCategoryId] = useState<number>(
    expense?.category.id ?? (categories.length > 0 ? categories[0].id : 0),
  );
  const [amount, setAmount] = useState(expense?.amount?.toString() ?? '');
  const [description, setDescription] = useState(expense?.description ?? '');
  const [expenseDate, setExpenseDate] = useState(
    expense?.expenseDate ?? new Date().toISOString().slice(0, 10),
  );
  const [memo, setMemo] = useState(expense?.memo ?? '');
  const [error, setError] = useState('');

  const queryClient = useQueryClient();

  const createMutation = useMutation({
    mutationFn: (data: CreateExpenseRequest) => createExpense(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['expenses'] });
      onClose();
    },
    onError: () => setError('지출 등록에 실패했습니다.'),
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateExpenseRequest }) =>
      updateExpense(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['expenses'] });
      onClose();
    },
    onError: () => setError('지출 수정에 실패했습니다.'),
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    const parsedAmount = Number(amount);
    if (!parsedAmount || parsedAmount <= 0) {
      setError('금액을 입력해주세요.');
      return;
    }
    if (!categoryId) {
      setError('카테고리를 선택해주세요.');
      return;
    }
    if (!description.trim()) {
      setError('설명을 입력해주세요.');
      return;
    }
    if (!expenseDate) {
      setError('날짜를 선택해주세요.');
      return;
    }

    const payload = {
      categoryId,
      amount: parsedAmount,
      description: description.trim(),
      expenseDate,
      memo: memo.trim() || undefined,
    };

    if (isEdit && expense) {
      updateMutation.mutate({ id: expense.id, data: payload });
    } else {
      createMutation.mutate(payload);
    }
  };

  const isPending = createMutation.isPending || updateMutation.isPending;

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={isEdit ? '지출 수정' : '지출 등록'}>
      <form onSubmit={handleSubmit} className="space-y-4">
        {/* Amount */}
        <Input
          label="금액"
          type="number"
          placeholder="0"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
          required
        />

        {/* Category */}
        <div className="flex flex-col gap-1">
          <label className="text-sm font-medium text-gray-700">카테고리</label>
          <select
            value={categoryId}
            onChange={(e) => setCategoryId(Number(e.target.value))}
            className="rounded-lg border border-gray-300 px-3 py-2 text-gray-900 focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500"
          >
            <option value={0} disabled>
              카테고리 선택
            </option>
            {categories.map((cat) => (
              <option key={cat.id} value={cat.id}>
                {cat.name}
              </option>
            ))}
          </select>
        </div>

        {/* Date */}
        <Input
          label="날짜"
          type="date"
          value={expenseDate}
          onChange={(e) => setExpenseDate(e.target.value)}
          required
        />

        {/* Description */}
        <Input
          label="설명"
          type="text"
          placeholder="지출 내용을 입력하세요"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          required
        />

        {/* Memo */}
        <Input
          label="메모"
          type="text"
          placeholder="메모 (선택)"
          value={memo}
          onChange={(e) => setMemo(e.target.value)}
        />

        {error && <p className="text-sm text-red-500">{error}</p>}

        {/* Actions */}
        <div className="flex justify-end gap-2">
          <Button type="button" variant="secondary" onClick={onClose}>
            취소
          </Button>
          <Button type="submit" disabled={isPending}>
            {isPending ? '저장 중...' : isEdit ? '수정' : '등록'}
          </Button>
        </div>
      </form>
    </Modal>
  );
}
