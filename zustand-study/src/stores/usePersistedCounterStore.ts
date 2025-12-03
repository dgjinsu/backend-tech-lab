/**
 * Persisted Counter Store
 *
 * localStorage와 연동되는 카운터 상태 관리
 */

import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface PersistedCounterState {
  count: number;
  increment: () => void;
  decrement: () => void;
}

// localStorage와 연동하면서도 타입 안정성과 자동 동기화 제공
export const usePersistedCounterStore = create<PersistedCounterState>()(
  persist(
    (set) => ({
      count: 0,
      increment: () => set((state) => ({ count: state.count + 1 })),
      decrement: () => set((state) => ({ count: state.count - 1 })),
    }),
    {
      name: 'counter-storage', // localStorage 키
    }
  )
);
