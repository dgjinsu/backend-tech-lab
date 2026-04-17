import type { ReactNode } from 'react';
import type { Expense } from '../types/expense';
import { StatusBadge } from './StatusBadge';
import { formatAmount, formatDateTime } from '../lib/format';

interface Props {
  rows: Expense[];
  renderActions?: (row: Expense) => ReactNode;
  highlightOwnerId?: number;
}

export function ExpenseTable({ rows, renderActions, highlightOwnerId }: Props) {
  if (rows.length === 0) {
    return (
      <div className="rounded-xl border border-dashed border-slate-300 bg-white p-10 text-center text-sm text-slate-500">
        볼 수 있는 지출이 없습니다.
      </div>
    );
  }

  return (
    <div className="overflow-hidden rounded-xl border border-slate-200 bg-white">
      <table className="min-w-full divide-y divide-slate-200 text-sm">
        <thead className="bg-slate-50 text-slate-600">
          <tr>
            <th className="px-4 py-2 text-left font-medium">ID</th>
            <th className="px-4 py-2 text-left font-medium">Owner</th>
            <th className="px-4 py-2 text-left font-medium">Dept</th>
            <th className="px-4 py-2 text-right font-medium">Amount</th>
            <th className="px-4 py-2 text-left font-medium">Description</th>
            <th className="px-4 py-2 text-left font-medium">Status</th>
            <th className="px-4 py-2 text-left font-medium">Created</th>
            {renderActions && <th className="px-4 py-2 text-right font-medium">Actions</th>}
          </tr>
        </thead>
        <tbody className="divide-y divide-slate-100">
          {rows.map((e) => {
            const isOwn = highlightOwnerId !== undefined && e.ownerId === highlightOwnerId;
            return (
              <tr key={e.id} className="hover:bg-slate-50">
                <td className="px-4 py-2 font-mono text-slate-900">{e.id}</td>
                <td className="px-4 py-2 text-slate-700">
                  {e.ownerId}
                  {isOwn && <span className="ml-1 text-xs text-slate-500">(me)</span>}
                </td>
                <td className="px-4 py-2 text-slate-700">{e.departmentId}</td>
                <td className="px-4 py-2 text-right font-medium text-slate-900">
                  {formatAmount(e.amount)}
                </td>
                <td className="px-4 py-2 text-slate-700">{e.description}</td>
                <td className="px-4 py-2">
                  <StatusBadge status={e.status} />
                </td>
                <td className="px-4 py-2 text-slate-500">{formatDateTime(e.createdAt)}</td>
                {renderActions && (
                  <td className="px-4 py-2 text-right">{renderActions(e)}</td>
                )}
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}
