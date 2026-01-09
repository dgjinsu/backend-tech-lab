/**
 * 카운터를 표시하고 조작하는 컴포넌트
 */

import { useAtomValue, useSetAtom } from 'jotai';
import { countAtom, incrementAtom, decrementAtom, resetAtom } from '../../atoms/counterAtoms';

export function CounterDisplay() {
  // 필요한 상태와 액션을 개별적으로 구독
  const count = useAtomValue(countAtom);
  const increment = useSetAtom(incrementAtom);
  const decrement = useSetAtom(decrementAtom);
  const reset = useSetAtom(resetAtom);

  console.log('CounterDisplay 렌더링 (Jotai)');

  return (
    <div className="p-4 border border-purple-500 rounded-lg flex-1">
      <h3 className="text-lg font-semibold mb-2">카운터 1</h3>
      <p className="mb-3">카운트: {count}</p>
      <button
        onClick={increment}
        className="px-4 py-2 bg-purple-500 text-white rounded hover:bg-purple-600 transition-colors"
      >
        증가
      </button>
      <button
        onClick={decrement}
        className="ml-2 px-4 py-2 bg-purple-500 text-white rounded hover:bg-purple-600 transition-colors"
      >
        감소
      </button>
      <button
        onClick={reset}
        className="ml-2 px-4 py-2 bg-purple-500 text-white rounded hover:bg-purple-600 transition-colors"
      >
        리셋
      </button>
    </div>
  );
}
