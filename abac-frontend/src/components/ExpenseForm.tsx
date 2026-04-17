import { useState, type FormEvent } from 'react';
import { Button } from './ui/Button';
import { Input } from './ui/Input';

export interface ExpenseFormValues {
  amount: string;
  description: string;
}

interface Props {
  initial?: Partial<ExpenseFormValues>;
  submitLabel: string;
  onSubmit: (values: ExpenseFormValues) => Promise<void> | void;
  onCancel: () => void;
}

export function ExpenseForm({ initial, submitLabel, onSubmit, onCancel }: Props) {
  const [amount, setAmount] = useState(initial?.amount ?? '');
  const [description, setDescription] = useState(initial?.description ?? '');
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handle = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      await onSubmit({ amount, description });
    } catch (err) {
      setError(err instanceof Error ? err.message : '요청 실패');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form onSubmit={handle} className="space-y-4">
      <Input
        label="금액 (KRW)"
        type="number"
        step="0.01"
        min="0.01"
        required
        value={amount}
        onChange={(e) => setAmount(e.target.value)}
      />
      <Input
        label="설명"
        required
        value={description}
        onChange={(e) => setDescription(e.target.value)}
      />
      {error && <p className="rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">{error}</p>}
      <div className="flex justify-end gap-2">
        <Button type="button" variant="secondary" onClick={onCancel}>
          취소
        </Button>
        <Button type="submit" disabled={submitting}>
          {submitting ? '처리 중…' : submitLabel}
        </Button>
      </div>
    </form>
  );
}
