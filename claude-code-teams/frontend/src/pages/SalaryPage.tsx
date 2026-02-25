import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { getSalaries, getSalary, createSalary, updateSalary, deleteSalary } from '../api/salary';
import type { Salary, CreateSalaryRequest, UpdateSalaryRequest } from '../types/salary';
import { formatCurrency } from '../utils/format';
import Card from '../components/common/Card';
import Button from '../components/common/Button';
import Input from '../components/common/Input';
import Modal from '../components/common/Modal';
import Loading from '../components/common/Loading';

interface FixedExpenseInput {
  name: string;
  amount: string;
}

const MONTHS = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12] as const;

export default function SalaryPage() {
  const currentYear = new Date().getFullYear();
  const [selectedYear, setSelectedYear] = useState(currentYear);
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [editingSalary, setEditingSalary] = useState<Salary | null>(null);
  const [deleteTarget, setDeleteTarget] = useState<Salary | null>(null);

  const queryClient = useQueryClient();

  const { data: salaries, isLoading } = useQuery({
    queryKey: ['salaries', selectedYear],
    queryFn: () => getSalaries(selectedYear).then((res) => res.data.data),
  });

  const handleCardClick = async (salaryId: number) => {
    try {
      const res = await getSalary(salaryId);
      setEditingSalary(res.data.data);
    } catch {
      // handle error silently
    }
  };

  const deleteMutation = useMutation({
    mutationFn: (id: number) => deleteSalary(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['salaries', selectedYear] });
      setDeleteTarget(null);
    },
  });

  const sortedSalaries = salaries?.slice().sort((a, b) => a.month - b.month);

  return (
    <div>
      {/* Header */}
      <div className="mb-6 flex items-center justify-between">
        <h2 className="text-2xl font-bold text-gray-900">급여 관리</h2>
        <Button onClick={() => setIsCreateModalOpen(true)}>+ 급여 등록</Button>
      </div>

      {/* Year Selector */}
      <div className="mb-6 flex items-center justify-center gap-4">
        <Button variant="secondary" size="sm" onClick={() => setSelectedYear((y) => y - 1)}>
          &larr;
        </Button>
        <span className="text-lg font-semibold text-gray-800">{selectedYear}년</span>
        <Button variant="secondary" size="sm" onClick={() => setSelectedYear((y) => y + 1)}>
          &rarr;
        </Button>
      </div>

      {/* Content */}
      {isLoading ? (
        <Loading />
      ) : !sortedSalaries || sortedSalaries.length === 0 ? (
        <Card>
          <p className="py-12 text-center text-gray-400">
            {selectedYear}년 등록된 급여 정보가 없습니다.
          </p>
        </Card>
      ) : (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {sortedSalaries.map((salary) => (
            <Card
              key={salary.id}
              className="cursor-pointer transition-shadow hover:shadow-md"
            >
              <div onClick={() => handleCardClick(salary.id)}>
                <div className="mb-3 flex items-center justify-between">
                  <h3 className="text-lg font-semibold text-gray-900">{salary.month}월</h3>
                </div>
                <div className="space-y-1 text-sm text-gray-600">
                  <div className="flex justify-between">
                    <span>총 급여</span>
                    <span className="font-medium text-gray-900">
                      {formatCurrency(salary.totalAmount)}
                    </span>
                  </div>
                  <div className="flex justify-between">
                    <span>고정 지출</span>
                    <span className="font-medium text-gray-900">
                      {formatCurrency(salary.fixedExpenseTotal)}
                    </span>
                  </div>
                  <div className="mt-2 flex justify-between border-t border-gray-100 pt-2">
                    <span className="font-medium">가용 용돈</span>
                    <span className="font-bold text-green-600">
                      {formatCurrency(salary.availableAmount)}
                    </span>
                  </div>
                </div>
              </div>
              <div className="mt-3 flex justify-end">
                <Button
                  variant="danger"
                  size="sm"
                  onClick={(e) => {
                    e.stopPropagation();
                    setDeleteTarget(salary);
                  }}
                >
                  삭제
                </Button>
              </div>
            </Card>
          ))}
        </div>
      )}

      {/* Create Modal */}
      <SalaryFormModal
        isOpen={isCreateModalOpen}
        onClose={() => setIsCreateModalOpen(false)}
        year={selectedYear}
        existingMonths={salaries?.map((s) => s.month) ?? []}
      />

      {/* Edit Modal */}
      {editingSalary && (
        <SalaryFormModal
          isOpen={true}
          onClose={() => setEditingSalary(null)}
          salary={editingSalary}
          year={selectedYear}
          existingMonths={[]}
        />
      )}

      {/* Delete Confirmation */}
      <Modal
        isOpen={deleteTarget !== null}
        onClose={() => setDeleteTarget(null)}
        title="급여 삭제"
      >
        <p className="mb-4 text-gray-600">
          {deleteTarget?.year}년 {deleteTarget?.month}월 급여를 삭제하시겠습니까?
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

// ─── Salary Form Modal ──────────────────────────────────────

interface SalaryFormModalProps {
  isOpen: boolean;
  onClose: () => void;
  year: number;
  existingMonths: number[];
  salary?: Salary;
}

function SalaryFormModal({ isOpen, onClose, year, existingMonths, salary }: SalaryFormModalProps) {
  const isEdit = !!salary;

  const [month, setMonth] = useState<number>(salary?.month ?? getNextAvailableMonth(existingMonths));
  const [totalAmount, setTotalAmount] = useState(salary?.totalAmount?.toString() ?? '');
  const [fixedExpenses, setFixedExpenses] = useState<FixedExpenseInput[]>(
    salary?.fixedExpenses?.map((fe) => ({ name: fe.name, amount: fe.amount.toString() })) ?? [
      { name: '', amount: '' },
    ],
  );
  const [memo, setMemo] = useState(salary?.memo ?? '');
  const [error, setError] = useState('');

  const queryClient = useQueryClient();

  const createMutation = useMutation({
    mutationFn: (data: CreateSalaryRequest) => createSalary(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['salaries'] });
      onClose();
    },
    onError: () => setError('급여 등록에 실패했습니다.'),
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateSalaryRequest }) => updateSalary(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['salaries'] });
      onClose();
    },
    onError: () => setError('급여 수정에 실패했습니다.'),
  });

  const parsedTotal = Number(totalAmount) || 0;
  const fixedExpenseTotal = fixedExpenses.reduce((sum, fe) => sum + (Number(fe.amount) || 0), 0);
  const availableAmount = parsedTotal - fixedExpenseTotal;

  const addFixedExpense = () => {
    setFixedExpenses([...fixedExpenses, { name: '', amount: '' }]);
  };

  const removeFixedExpense = (index: number) => {
    setFixedExpenses(fixedExpenses.filter((_, i) => i !== index));
  };

  const updateFixedExpense = (index: number, field: keyof FixedExpenseInput, value: string) => {
    setFixedExpenses(fixedExpenses.map((fe, i) => (i === index ? { ...fe, [field]: value } : fe)));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (parsedTotal <= 0) {
      setError('총 급여액을 입력해주세요.');
      return;
    }

    const validExpenses = fixedExpenses
      .filter((fe) => fe.name.trim() && Number(fe.amount) > 0)
      .map((fe) => ({ name: fe.name.trim(), amount: Number(fe.amount) }));

    if (isEdit && salary) {
      updateMutation.mutate({
        id: salary.id,
        data: {
          totalAmount: parsedTotal,
          fixedExpenses: validExpenses,
          memo: memo || undefined,
        },
      });
    } else {
      createMutation.mutate({
        year,
        month,
        totalAmount: parsedTotal,
        fixedExpenses: validExpenses,
        memo: memo || undefined,
      });
    }
  };

  const isPending = createMutation.isPending || updateMutation.isPending;

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={isEdit ? '급여 수정' : '급여 등록'}>
      <form onSubmit={handleSubmit} className="space-y-4">
        {/* Year & Month (create only) */}
        {!isEdit && (
          <div className="flex gap-3">
            <div className="flex-1">
              <label className="mb-1 block text-sm font-medium text-gray-700">연도</label>
              <input
                type="number"
                value={year}
                disabled
                className="w-full rounded-lg border border-gray-300 bg-gray-50 px-3 py-2 text-gray-500"
              />
            </div>
            <div className="flex-1">
              <label className="mb-1 block text-sm font-medium text-gray-700">월</label>
              <select
                value={month}
                onChange={(e) => setMonth(Number(e.target.value))}
                className="w-full rounded-lg border border-gray-300 px-3 py-2 text-gray-900 focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500"
              >
                {MONTHS.map((m) => (
                  <option key={m} value={m} disabled={existingMonths.includes(m)}>
                    {m}월 {existingMonths.includes(m) ? '(등록됨)' : ''}
                  </option>
                ))}
              </select>
            </div>
          </div>
        )}

        {/* Total Amount */}
        <Input
          label="총 급여액"
          type="number"
          placeholder="0"
          value={totalAmount}
          onChange={(e) => setTotalAmount(e.target.value)}
          required
        />

        {/* Fixed Expenses */}
        <div>
          <div className="mb-2 flex items-center justify-between">
            <label className="text-sm font-medium text-gray-700">고정 지출</label>
            <Button type="button" variant="secondary" size="sm" onClick={addFixedExpense}>
              + 항목 추가
            </Button>
          </div>
          <div className="space-y-2">
            {fixedExpenses.map((fe, index) => (
              <div key={index} className="flex items-center gap-2">
                <input
                  type="text"
                  placeholder="항목명"
                  value={fe.name}
                  onChange={(e) => updateFixedExpense(index, 'name', e.target.value)}
                  className="flex-1 rounded-lg border border-gray-300 px-3 py-2 text-sm text-gray-900 placeholder-gray-400 focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500"
                />
                <input
                  type="number"
                  placeholder="금액"
                  value={fe.amount}
                  onChange={(e) => updateFixedExpense(index, 'amount', e.target.value)}
                  className="w-28 rounded-lg border border-gray-300 px-3 py-2 text-sm text-gray-900 placeholder-gray-400 focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500"
                />
                <button
                  type="button"
                  onClick={() => removeFixedExpense(index)}
                  className="text-gray-400 hover:text-red-500"
                >
                  ✕
                </button>
              </div>
            ))}
          </div>
        </div>

        {/* Memo */}
        <Input
          label="메모"
          type="text"
          placeholder="메모 (선택)"
          value={memo}
          onChange={(e) => setMemo(e.target.value)}
        />

        {/* Real-time Calculation */}
        <div className="rounded-lg bg-gray-50 p-3 text-sm">
          <div className="flex justify-between text-gray-600">
            <span>총 급여</span>
            <span>{formatCurrency(parsedTotal)}</span>
          </div>
          <div className="flex justify-between text-gray-600">
            <span>고정 지출 합계</span>
            <span>-{formatCurrency(fixedExpenseTotal)}</span>
          </div>
          <div className="mt-1 flex justify-between border-t border-gray-200 pt-1 font-medium">
            <span>가용 용돈</span>
            <span className={availableAmount >= 0 ? 'text-green-600' : 'text-red-600'}>
              {formatCurrency(availableAmount)}
            </span>
          </div>
        </div>

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

function getNextAvailableMonth(existingMonths: number[]): number {
  const currentMonth = new Date().getMonth() + 1;
  if (!existingMonths.includes(currentMonth)) return currentMonth;
  for (const m of MONTHS) {
    if (!existingMonths.includes(m)) return m;
  }
  return 1;
}
