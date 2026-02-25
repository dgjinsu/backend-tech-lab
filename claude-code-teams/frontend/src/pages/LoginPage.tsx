import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import Button from '../components/common/Button';
import Input from '../components/common/Input';

const PASSWORD_REGEX = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?]).{8,}$/;

export default function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [passwordHint, setPasswordHint] = useState('');
  const { login, loading } = useAuth();

  const validatePassword = (value: string) => {
    if (value.length === 0) {
      setPasswordHint('');
      return;
    }
    if (value.length < 8) {
      setPasswordHint('비밀번호는 8자 이상이어야 합니다.');
      return;
    }
    if (!PASSWORD_REGEX.test(value)) {
      setPasswordHint('영문, 숫자, 특수문자를 포함해야 합니다.');
      return;
    }
    setPasswordHint('');
  };

  const handlePasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setPassword(value);
    validatePassword(value);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    try {
      await login({ email, password });
    } catch {
      setError('이메일 또는 비밀번호가 올바르지 않습니다.');
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-50">
      <div className="w-full max-w-sm rounded-xl bg-white p-8 shadow-sm border border-gray-100">
        <h1 className="mb-6 text-center text-2xl font-bold text-indigo-600">가계부</h1>
        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          <Input
            label="이메일"
            type="email"
            placeholder="email@example.com"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
          <div>
            <Input
              label="비밀번호"
              type="password"
              placeholder="비밀번호 입력"
              value={password}
              onChange={handlePasswordChange}
              required
            />
            {passwordHint && (
              <p className="mt-1 text-xs text-amber-600">{passwordHint}</p>
            )}
          </div>
          {error && <p className="text-sm text-red-500">{error}</p>}
          <Button type="submit" className="w-full" disabled={loading}>
            {loading ? '로그인 중...' : '로그인'}
          </Button>
        </form>
        <p className="mt-4 text-center text-sm text-gray-500">
          계정이 없으신가요?{' '}
          <Link to="/signup" className="text-indigo-600 hover:underline">회원가입</Link>
        </p>
      </div>
    </div>
  );
}
