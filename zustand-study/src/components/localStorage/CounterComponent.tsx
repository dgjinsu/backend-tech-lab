/**
 * 첫 번째 카운터 컴포넌트
 */

import { useState, useEffect } from 'react';

export function CounterComponent() {
  const [count, setCount] = useState(() => {
    const saved = localStorage.getItem('counter');
    return saved ? parseInt(saved) : 0;
  });

  useEffect(() => {
    // 동기적으로 실행되어 메인 스레드를 블로킹
    localStorage.setItem('counter', count.toString());
  }, [count]);

  // 다른 컴포넌트의 변경사항을 수신하기 위한 이벤트 리스너
  // 하지만 같은 탭에서는 작동하지 않음!
  useEffect(() => {
    const handleStorageChange = (e: StorageEvent) => {
      if (e.key === 'counter' && e.newValue) {
        setCount(parseInt(e.newValue));
      }
    };

    window.addEventListener('storage', handleStorageChange);
    return () => window.removeEventListener('storage', handleStorageChange);
  }, []);

  return (
    <div className="p-4 border border-yellow-500 rounded-lg flex-1">
      <h3 className="text-lg font-semibold mb-2">카운터 1</h3>
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
        ⚠️ 버튼을 빠르게 여러 번 클릭하면 블로킹을 느낄 수 있습니다
      </p>
    </div>
  );
}
