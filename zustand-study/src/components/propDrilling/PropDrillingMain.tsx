/**
 * Props Drilling 메인 컴포넌트
 */

import { useState } from 'react';
import { DepthOne } from './DepthOne';

export function PropDrillingMain() {
  const [count, setCount] = useState(0);
  const [user, setUser] = useState({ name: '홍길동', role: 'user' });

  return (
    <div className="p-5 border-2 border-blue-500 rounded-lg">
      <h2 className="text-2xl font-bold mb-4">🔴 Props Drilling 방식</h2>
      <p className="text-red-600 mb-4">
        문제: count와 user 상태를 깊은 하위 컴포넌트까지 전달하기 위해
        <br />
        중간의 모든 컴포넌트가 props를 전달해야 합니다.
      </p>

      <DepthOne
        count={count}
        setCount={setCount}
        user={user}
        setUser={setUser}
      />

      <div className="mt-5 p-4 bg-yellow-100 rounded">
        <strong>⚠️ Props Drilling의 문제점:</strong>
        <ul className="mt-1 text-sm list-disc list-inside">
          <li>새로운 상태를 추가하면 중간 컴포넌트들을 모두 수정해야 함</li>
          <li>중간 컴포넌트가 불필요한 props로 복잡해짐</li>
          <li>리팩토링이 어려워짐</li>
          <li>컴포넌트의 재사용성이 떨어짐</li>
        </ul>
      </div>
    </div>
  );
}
