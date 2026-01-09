/**
 * LocalStorage와 연동되는 카운터 Atoms
 */

import { atom } from 'jotai';
import { atomWithStorage } from 'jotai/utils';

// LocalStorage와 자동 동기화되는 atom
export const persistedCountAtom = atomWithStorage('jotai-counter-storage', 0);

// 증가 액션 (write-only atom)
export const persistedIncrementAtom = atom(
  null,
  (get, set) => {
    set(persistedCountAtom, get(persistedCountAtom) + 1);
  }
);

// 감소 액션 (write-only atom)
export const persistedDecrementAtom = atom(
  null,
  (get, set) => {
    set(persistedCountAtom, get(persistedCountAtom) - 1);
  }
);
