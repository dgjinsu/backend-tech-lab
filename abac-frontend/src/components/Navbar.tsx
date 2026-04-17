import type { ReactNode } from 'react';
import { useAuth } from '../auth/useAuth';

const ROLE_COLOR: Record<string, string> = {
  EMPLOYEE: 'bg-slate-100 text-slate-700',
  MANAGER: 'bg-blue-100 text-blue-700',
  FINANCE: 'bg-purple-100 text-purple-700',
  ADMIN: 'bg-amber-100 text-amber-800',
};

export function Navbar({ actions }: { actions?: ReactNode }) {
  const { user, signOut } = useAuth();
  if (!user) return null;

  return (
    <header className="border-b border-slate-200 bg-white">
      <div className="mx-auto flex max-w-6xl items-center justify-between gap-4 px-6 py-3">
        <div className="flex items-center gap-3">
          <h1 className="text-lg font-semibold text-slate-900">경비 관리</h1>
          <span className={`rounded-full px-2 py-0.5 text-xs font-medium ${ROLE_COLOR[user.role] ?? 'bg-slate-100'}`}>
            {user.role}
          </span>
          <span className="text-xs text-slate-500">
            {user.username} · dept {user.departmentId}
          </span>
        </div>
        <div className="flex items-center gap-2">
          {actions}
          <button
            onClick={signOut}
            className="rounded-lg border border-slate-300 bg-white px-3 py-1.5 text-sm text-slate-700 hover:bg-slate-50"
          >
            로그아웃
          </button>
        </div>
      </div>
    </header>
  );
}
