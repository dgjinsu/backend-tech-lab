/**
 * 사용자 정보 컴포넌트
 */

import { useStore } from '../../stores/useStore';

export function UserDisplay() {
  // user 관련 상태와 액션만 선택적으로 구독
  const user = useStore((state) => state.user);
  const toggleRole = useStore((state) => state.toggleRole);

  // 아래와 같이 가져오면 전체 구독됨
  // const { user, toggleRole } = useStore();

  console.log('UserDisplay 렌더링 (user만 구독)');

  return (
    <div className="mt-5 p-4 border border-green-500 rounded-lg">
      <h3 className="text-lg font-semibold mb-2">사용자 정보</h3>
      <p className="mb-1">이름: {user.name}</p>
      <p className="mb-3">역할: {user.role}</p>
      <button
        onClick={toggleRole}
        className="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600 transition-colors"
      >
        역할 변경
      </button>
      <p className="text-xs text-green-700 mt-2">
        ✅ count가 변경되어도 이 컴포넌트는 리렌더링 안 됨
      </p>
    </div>
  );
}
