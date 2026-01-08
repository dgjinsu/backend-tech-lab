/**
 * Jotai 최적화된 카운터 컴포넌트
 *
 * useAtom vs useAtomValue vs useSetAtom 차이점 데모
 */

import { useAtom, useAtomValue, useSetAtom } from 'jotai';
import { countAtom, doubleCountAtom, incrementAtom } from '../../stores/atoms';

export function OptimizedCounterDisplay() {
  // useAtom: 읽기 + 쓰기 둘 다 필요할 때
  const [count, setCount] = useAtom(countAtom);

  // 파생 atom 사용 - count가 변경되면 자동으로 재계산
  const doubleCount = useAtomValue(doubleCountAtom);

  // 쓰기 전용 atom 사용
  const increment = useSetAtom(incrementAtom);

  console.log('Jotai OptimizedCounterDisplay 렌더링');

  return (
    <div className="p-4 border border-purple-500 rounded-lg flex-1">
      <h3 className="text-lg font-semibold mb-2">카운터 2 (최적화)</h3>
      <p className="mb-1">카운트: {count}</p>
      <p className="mb-3 text-purple-600">2배 값: {doubleCount}</p>
      <button
        onClick={() => increment()}
        className="px-4 py-2 bg-purple-500 text-white rounded hover:bg-purple-600 transition-colors"
      >
        증가 (atom)
      </button>
      <button
        onClick={() => setCount((prev) => prev + 5)}
        className="ml-2 px-4 py-2 bg-purple-500 text-white rounded hover:bg-purple-600 transition-colors"
      >
        +5 (직접 set)
      </button>
      <p className="text-xs text-purple-700 mt-2">
        doubleCountAtom은 파생 atom으로 count 변경 시 자동 계산
      </p>
    </div>
  );
}
