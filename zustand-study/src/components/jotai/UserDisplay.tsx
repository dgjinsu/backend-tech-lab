/**
 * Jotai 사용자 정보 컴포넌트
 */

import { useAtomValue, useSetAtom } from 'jotai';
import { userAtom, toggleRoleAtom } from '../../stores/atoms';

export function UserDisplay() {
  // 읽기 전용으로 user atom 구독
  const user = useAtomValue(userAtom);

  // 쓰기 전용 atom - 이 컴포넌트가 호출해도 리렌더링 원인이 되지 않음
  const toggleRole = useSetAtom(toggleRoleAtom);

  console.log('Jotai UserDisplay 렌더링');

  return (
    <div className="mt-5 p-4 border border-purple-500 rounded-lg">
      <h3 className="text-lg font-semibold mb-2">사용자 정보</h3>
      <p className="mb-1">이름: {user.name}</p>
      <p className="mb-3">역할: {user.role}</p>
      <button
        onClick={() => toggleRole()}
        className="px-4 py-2 bg-purple-500 text-white rounded hover:bg-purple-600 transition-colors"
      >
        역할 변경
      </button>
      <p className="text-xs text-purple-700 mt-2">
        count atom이 변경되어도 이 컴포넌트는 리렌더링 안 됨
      </p>
    </div>
  );
}
