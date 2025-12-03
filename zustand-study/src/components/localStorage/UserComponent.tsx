/**
 * 사용자 정보 컴포넌트
 */

import { useState, useEffect } from 'react';

export function UserComponent() {
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('user');
    // JSON.parse는 실패할 수 있고, 타입 안정성이 없음
    try {
      return saved ? JSON.parse(saved) : { name: '홍길동', role: 'user' };
    } catch {
      return { name: '홍길동', role: 'user' };
    }
  });

  // 로컬 상태로 입력값 관리
  const [name, setName] = useState(user.name);
  const [role, setRole] = useState(user.role);

  useEffect(() => {
    // 객체를 저장할 때마다 직렬화 비용 발생
    localStorage.setItem('user', JSON.stringify(user));
  }, [user]);

  const handleSave = () => {
    setUser({ name, role });
  };

  return (
    <div className="mt-5 p-4 border border-yellow-500 rounded-lg">
      <h3 className="text-lg font-semibold mb-2">사용자 정보</h3>

      <div className="mb-3">
        <label className="block text-sm font-medium mb-1">이름:</label>
        <input
          type="text"
          value={name}
          onChange={(e) => setName(e.target.value)}
          className="w-full px-3 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-yellow-500"
        />
      </div>

      <div className="mb-3">
        <label className="block text-sm font-medium mb-1">역할:</label>
        <input
          type="text"
          value={role}
          onChange={(e) => setRole(e.target.value)}
          className="w-full px-3 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-yellow-500"
        />
      </div>

      <button
        onClick={handleSave}
        className="px-4 py-2 bg-yellow-500 text-white rounded hover:bg-yellow-600 transition-colors"
      >
        저장
      </button>

      <div className="mt-3 p-2 bg-yellow-50 rounded text-sm">
        <p className="font-medium">저장된 값:</p>
        <p>이름: {user.name}</p>
        <p>역할: {user.role}</p>
      </div>

      <p className="text-xs text-gray-600 mt-2">
        ⚠️ 객체를 저장하려면 JSON.stringify/parse 필요 (타입 안정성 없음)
      </p>
    </div>
  );
}
