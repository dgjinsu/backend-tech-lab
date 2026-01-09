/**
 * 사용자 정보 관리 Atoms
 */

import { atom } from 'jotai';

interface User {
  name: string;
  role: string;
}

// 사용자 정보를 저장하는 기본 atom
export const userAtom = atom<User>({
  name: '홍길동',
  role: 'user',
});

// 역할 토글 액션 (write-only atom)
export const toggleRoleAtom = atom(
  null,
  (get, set) => {
    const currentUser = get(userAtom);
    set(userAtom, {
      ...currentUser,
      role: currentUser.role === 'user' ? 'admin' : 'user',
    });
  }
);
