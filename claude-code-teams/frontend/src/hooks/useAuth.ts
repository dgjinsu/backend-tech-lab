import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../stores/authStore';
import { login as loginApi, signup as signupApi } from '../api/auth';
import type { LoginRequest, SignupRequest } from '../types/auth';

export function useAuth() {
  const { token, user, setAuth, logout: storeLogout } = useAuthStore();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);

  const login = async (data: LoginRequest) => {
    setLoading(true);
    try {
      const res = await loginApi(data);
      const { accessToken, refreshToken, user: resUser } = res.data.data;
      setAuth(accessToken, refreshToken, resUser);
      navigate('/dashboard');
    } finally {
      setLoading(false);
    }
  };

  const signup = async (data: SignupRequest) => {
    setLoading(true);
    try {
      await signupApi(data);
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    storeLogout();
    navigate('/login');
  };

  return {
    token,
    user,
    isAuthenticated: !!token,
    loading,
    login,
    signup,
    logout,
  };
}
