/**
 * 두 번째 카운터 컴포넌트 (동일한 localStorage 키 사용)
 */

import { useState, useEffect } from 'react';

export function AnotherCounterComponent() {
  const [count, setCount] = useState(() => {
    const saved = localStorage.getItem('counter');
    return saved ? parseInt(saved) : 0;
  });

  useEffect(() => {
    localStorage.setItem('counter', count.toString());
  }, [count]);

  // 같은 탭 내에서 다른 컴포넌트의 변경을 감지하려면 추가 작업 필요
  // CustomEvent나 polling을 사용해야 함
  useEffect(() => {
    const interval = setInterval(() => {
      const saved = localStorage.getItem('counter');
      if (saved) {
        const newCount = parseInt(saved);
        if (newCount !== count) {
          setCount(newCount);
        }
      }
    }, 100); // 100ms마다 polling - 비효율적!

    return () => clearInterval(interval);
  }, [count]);

  return (
    <div className="p-4 border border-yellow-500 rounded-lg flex-1">
      <h3 className="text-lg font-semibold mb-2">카운터 2</h3>
      <p className="mb-3">카운트: {count}</p>
      <button
        onClick={() => setCount(count + 1)}
        className="px-4 py-2 bg-yellow-500 text-white rounded hover:bg-yellow-600 transition-colors"
      >
        증가
      </button>
      <button
        onClick={() => setCount(count - 1)}
        className="ml-2 px-4 py-2 bg-yellow-500 text-white rounded hover:bg-yellow-600 transition-colors"
      >
        감소
      </button>
      <p className="text-xs text-gray-600 mt-2">
        ⚠️ polling(100ms)으로 동기화 - 비효율적이고 정확하지 않음
      </p>
    </div>
  );
}
