import { useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { login } from '../api/auth';
import { useAuth } from '../auth/useAuth';
import { AxiosError } from 'axios';

const SEED_ACCOUNTS = [
  { username: 'emp1', role: 'EMPLOYEE', dept: 'Engineering' },
  { username: 'emp2', role: 'EMPLOYEE', dept: 'Sales' },
  { username: 'mgr1', role: 'MANAGER', dept: 'Engineering' },
  { username: 'fin1', role: 'FINANCE', dept: 'Finance' },
  { username: 'admin1', role: 'ADMIN', dept: 'Finance' },
];

export function LoginPage() {
  const [username, setUsername] = useState('emp1');
  const [password, setPassword] = useState('pass');
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const { signIn } = useAuth();
  const navigate = useNavigate();

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      const res = await login(username, password);
      signIn(res);
      navigate('/expenses', { replace: true });
    } catch (err) {
      if (err instanceof AxiosError && err.response?.status === 401) {
        setError('아이디 또는 비밀번호가 올바르지 않습니다.');
      } else {
        setError('로그인에 실패했습니다. 잠시 후 다시 시도하세요.');
      }
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center px-4">
      <div className="w-full max-w-md">
        <div className="rounded-2xl border border-slate-200 bg-white p-8 shadow-sm">
          <h1 className="text-2xl font-semibold text-slate-900">abac</h1>
          <p className="mt-1 text-sm text-slate-500">RBAC + ABAC 경비 관리 — 학습 콘솔</p>

          <form onSubmit={onSubmit} className="mt-6 space-y-4">
            <label className="block">
              <span className="text-sm font-medium text-slate-700">Username</span>
              <input
                className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2 text-sm focus:border-slate-500 focus:outline-none"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                autoComplete="username"
                required
              />
            </label>
            <label className="block">
              <span className="text-sm font-medium text-slate-700">Password</span>
              <input
                type="password"
                className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2 text-sm focus:border-slate-500 focus:outline-none"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                autoComplete="current-password"
                required
              />
            </label>

            {error && (
              <p className="rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">{error}</p>
            )}

            <button
              type="submit"
              disabled={submitting}
              className="w-full rounded-lg bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-800 disabled:opacity-50"
            >
              {submitting ? '로그인 중…' : '로그인'}
            </button>
          </form>
        </div>

        <div className="mt-4 rounded-xl border border-slate-200 bg-slate-50 p-4 text-xs text-slate-600">
          <p className="mb-2 font-medium text-slate-700">시딩 계정 (비밀번호 모두 <code className="rounded bg-white px-1">pass</code>)</p>
          <ul className="space-y-1">
            {SEED_ACCOUNTS.map((a) => (
              <li key={a.username} className="flex items-center gap-2">
                <button
                  type="button"
                  onClick={() => setUsername(a.username)}
                  className="rounded bg-white px-2 py-0.5 font-mono text-slate-900 hover:bg-slate-100"
                >
                  {a.username}
                </button>
                <span className="text-slate-500">
                  {a.role} · {a.dept}
                </span>
              </li>
            ))}
          </ul>
        </div>
      </div>
    </div>
  );
}
