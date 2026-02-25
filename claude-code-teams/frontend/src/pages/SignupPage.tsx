import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import Button from '../components/common/Button';
import Input from '../components/common/Input';

const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const PASSWORD_REGEX = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?]).{8,}$/;

interface FormErrors {
  email?: string;
  nickname?: string;
  password?: string;
  confirmPassword?: string;
}

export default function SignupPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [nickname, setNickname] = useState('');
  const [errors, setErrors] = useState<FormErrors>({});
  const [serverError, setServerError] = useState('');
  const [success, setSuccess] = useState(false);
  const { signup, loading } = useAuth();
  const navigate = useNavigate();

  const validateField = (field: string, value: string): string | undefined => {
    switch (field) {
      case 'email':
        if (!value) return '이메일을 입력해주세요.';
        if (!EMAIL_REGEX.test(value)) return '올바른 이메일 형식이 아닙니다.';
        return undefined;
      case 'nickname':
        if (!value) return '닉네임을 입력해주세요.';
        if (value.length < 2 || value.length > 10) return '닉네임은 2~10자여야 합니다.';
        return undefined;
      case 'password':
        if (!value) return '비밀번호를 입력해주세요.';
        if (value.length < 8) return '비밀번호는 8자 이상이어야 합니다.';
        if (!PASSWORD_REGEX.test(value)) return '영문, 숫자, 특수문자를 포함해야 합니다.';
        return undefined;
      case 'confirmPassword':
        if (!value) return '비밀번호 확인을 입력해주세요.';
        if (value !== password) return '비밀번호가 일치하지 않습니다.';
        return undefined;
      default:
        return undefined;
    }
  };

  const handleBlur = (field: string, value: string) => {
    const error = validateField(field, value);
    setErrors((prev) => ({ ...prev, [field]: error }));
  };

  const validateAll = (): boolean => {
    const newErrors: FormErrors = {
      email: validateField('email', email),
      nickname: validateField('nickname', nickname),
      password: validateField('password', password),
      confirmPassword: validateField('confirmPassword', confirmPassword),
    };
    setErrors(newErrors);
    return !Object.values(newErrors).some(Boolean);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setServerError('');

    if (!validateAll()) return;

    try {
      await signup({ email, password, nickname });
      setSuccess(true);
      setTimeout(() => navigate('/login'), 2000);
    } catch {
      setServerError('회원가입에 실패했습니다. 다시 시도해주세요.');
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-50">
      <div className="w-full max-w-sm rounded-xl bg-white p-8 shadow-sm border border-gray-100">
        <h1 className="mb-6 text-center text-2xl font-bold text-indigo-600">회원가입</h1>

        {success ? (
          <div className="rounded-lg bg-green-50 p-4 text-center">
            <p className="text-green-700 font-medium">회원가입이 완료되었습니다!</p>
            <p className="mt-1 text-sm text-green-600">잠시 후 로그인 페이지로 이동합니다.</p>
          </div>
        ) : (
          <>
            <form onSubmit={handleSubmit} className="flex flex-col gap-4">
              <Input
                label="이메일"
                type="email"
                placeholder="email@example.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                onBlur={() => handleBlur('email', email)}
                error={errors.email}
                required
              />
              <Input
                label="닉네임"
                type="text"
                placeholder="2~10자"
                value={nickname}
                onChange={(e) => setNickname(e.target.value)}
                onBlur={() => handleBlur('nickname', nickname)}
                error={errors.nickname}
                required
              />
              <Input
                label="비밀번호"
                type="password"
                placeholder="8자 이상, 영문+숫자+특수문자"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                onBlur={() => handleBlur('password', password)}
                error={errors.password}
                required
              />
              <Input
                label="비밀번호 확인"
                type="password"
                placeholder="비밀번호를 다시 입력"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                onBlur={() => handleBlur('confirmPassword', confirmPassword)}
                error={errors.confirmPassword}
                required
              />
              {serverError && <p className="text-sm text-red-500">{serverError}</p>}
              <Button type="submit" className="w-full" disabled={loading}>
                {loading ? '가입 중...' : '가입하기'}
              </Button>
            </form>
            <p className="mt-4 text-center text-sm text-gray-500">
              이미 계정이 있으신가요?{' '}
              <Link to="/login" className="text-indigo-600 hover:underline">로그인</Link>
            </p>
          </>
        )}
      </div>
    </div>
  );
}
