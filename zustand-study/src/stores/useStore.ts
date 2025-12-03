/**
 * Main Store
 *
 * 카운터와 사용자 정보를 함께 관리하는 통합 Store
 */

import { create } from 'zustand';

interface User {
  name: string;
  role: string;
}

interface StoreState {
  // 카운터 상태
  count: number;
  increment: () => void;
  decrement: () => void;
  reset: () => void;

  // 사용자 상태
  user: User;
  toggleRole: () => void;
}

export const useStore = create<StoreState>((set) => ({
  // 카운터 초기 상태 및 액션
  count: 0,
  increment: () => set((state) => ({ count: state.count + 1 })),
  decrement: () => set((state) => ({ count: state.count - 1 })),
  reset: () => set({ count: 0 }),

  // 사용자 초기 상태 및 액션
  user: {
    name: '홍길동',
    role: 'user',
  },
  toggleRole: () =>
    set((state) => ({
      user: {
        ...state.user,
        role: state.user.role === 'user' ? 'admin' : 'user',
      },
    })),
}));
