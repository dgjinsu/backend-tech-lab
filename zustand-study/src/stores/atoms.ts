/**
 * Jotai Atoms
 *
 * 카운터와 사용자 정보를 관리하는 Jotai atoms
 * Zustand와 달리 각 상태를 독립적인 atom으로 분리하여 관리
 */

import { atom } from 'jotai';

// ============ 타입 정의 ============
interface User {
  name: string;
  role: string;
}

// ============ 카운터 Atoms ============
// 기본 atom - 원시 상태값
export const countAtom = atom(0);

// 파생 atom (derived atom) - 읽기 전용
export const doubleCountAtom = atom((get) => get(countAtom) * 2);

// 쓰기 전용 atom (write-only atom) - 액션
export const incrementAtom = atom(null, (get, set) => {
  set(countAtom, get(countAtom) + 1);
});

export const decrementAtom = atom(null, (get, set) => {
  set(countAtom, get(countAtom) - 1);
});

export const resetCountAtom = atom(null, (_get, set) => {
  set(countAtom, 0);
});

// ============ 사용자 Atoms ============
// 기본 atom - 객체 상태
export const userAtom = atom<User>({
  name: '홍길동',
  role: 'user',
});

// 파생 atom - 개별 필드 읽기
export const userNameAtom = atom((get) => get(userAtom).name);
export const userRoleAtom = atom((get) => get(userAtom).role);

// 쓰기 전용 atom - role 토글 액션
export const toggleRoleAtom = atom(null, (get, set) => {
  const currentUser = get(userAtom);
  set(userAtom, {
    ...currentUser,
    role: currentUser.role === 'user' ? 'admin' : 'user',
  });
});

// 읽기-쓰기 atom (read-write atom) - 더 유연한 패턴
export const userWithActionsAtom = atom(
  (get) => get(userAtom),
  (get, set, newName: string) => {
    const currentUser = get(userAtom);
    set(userAtom, { ...currentUser, name: newName });
  }
);
