/**
 * 카운터를 표시하고 조작하는 컴포넌트
 */

import { useStore } from '../../stores/useStore';

export function CounterDisplay() {
  // 필요한 상태와 액션만 하나씩 선택적으로 구독
  const count = useStore((state) => state.count);
  const increment = useStore((state) => state.increment);
  const decrement = useStore((state) => state.decrement);
  const reset = useStore((state) => state.reset);
  console.log('CounterDisplay 렌더링');

  return (
    <div className="p-4 border border-green-500 rounded-lg flex-1">
      <h3 className="text-lg font-semibold mb-2">카운터 1</h3>
      <p className="mb-3">카운트: {count}</p>
      <button
        onClick={increment}
        className="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600 transition-colors"
      >
        증가
      </button>
      <button
        onClick={decrement}
        className="ml-2 px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600 transition-colors"
      >
        감소
      </button>
      <button
        onClick={reset}
        className="ml-2 px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600 transition-colors"
      >
        리셋
      </button>
    </div>
  );
}
