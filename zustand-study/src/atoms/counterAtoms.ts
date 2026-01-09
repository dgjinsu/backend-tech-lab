/**
 * 카운터 상태 관리 Atoms
 */

import { atom } from 'jotai';

// 카운터 값을 저장하는 기본 atom
export const countAtom = atom(0);

// 증가 액션 (write-only atom)
export const incrementAtom = atom(
  null, // read 값 없음 (write-only)
  (get, set) => {
    set(countAtom, get(countAtom) + 1);
  }
);

// 감소 액션 (write-only atom)
export const decrementAtom = atom(
  null,
  (get, set) => {
    set(countAtom, get(countAtom) - 1);
  }
);

// 리셋 액션 (write-only atom)
export const resetAtom = atom(
  null,
  (_get, set) => {
    set(countAtom, 0);
  }
);
