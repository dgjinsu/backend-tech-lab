import { createContext, useCallback, useEffect, useMemo, useState, type ReactNode } from 'react';
import type { AuthUser, LoginResponse } from '../types/auth';
import { tokenStorage } from '../api/client';

const USER_KEY = 'abac.auth.user';

export interface AuthContextValue {
  user: AuthUser | null;
  isAuthenticated: boolean;
  signIn: (response: LoginResponse) => void;
  signOut: () => void;
}

export const AuthContext = createContext<AuthContextValue | undefined>(undefined);

function readStoredUser(): AuthUser | null {
  const raw = localStorage.getItem(USER_KEY);
  if (!raw) return null;
  try {
    return JSON.parse(raw) as AuthUser;
  } catch {
    return null;
  }
}

// JWT payload 디코드 — userId는 토큰 claim에만 있으므로 저장용으로 꺼낸다
function decodeUserIdFromToken(token: string): number | null {
  try {
    const payload = token.split('.')[1];
    const json = JSON.parse(atob(payload.replace(/-/g, '+').replace(/_/g, '/')));
    return typeof json.userId === 'number' ? json.userId : null;
  } catch {
    return null;
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(() => readStoredUser());

  useEffect(() => {
    if (user) {
      localStorage.setItem(USER_KEY, JSON.stringify(user));
    } else {
      localStorage.removeItem(USER_KEY);
    }
  }, [user]);

  const signIn = useCallback((response: LoginResponse) => {
    const userId = decodeUserIdFromToken(response.token);
    if (userId === null) {
      throw new Error('Invalid token payload: userId missing');
    }
    tokenStorage.set(response.token);
    setUser({
      userId,
      username: response.username,
      role: response.role,
      departmentId: response.departmentId,
    });
  }, []);

  const signOut = useCallback(() => {
    tokenStorage.clear();
    setUser(null);
  }, []);

  const value = useMemo<AuthContextValue>(
    () => ({ user, isAuthenticated: user !== null, signIn, signOut }),
    [user, signIn, signOut],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
