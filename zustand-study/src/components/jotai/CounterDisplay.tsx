/**
 * Jotai 카운터를 표시하고 조작하는 컴포넌트
 */

import { useAtom, useAtomValue, useSetAtom } from 'jotai';
import {
  countAtom,
  incrementAtom,
  decrementAtom,
  resetCountAtom,
} from '../../stores/atoms';

export function CounterDisplay() {
  // useAtomValue: 읽기만 할 때 (렌더링 최적화)
  const count = useAtomValue(countAtom);

  // useSetAtom: 쓰기만 할 때 (값 변경해도 리렌더링 안 됨)
  const increment = useSetAtom(incrementAtom);
  const decrement = useSetAtom(decrementAtom);
  const reset = useSetAtom(resetCountAtom);

  console.log('Jotai CounterDisplay 렌더링');

  return (
    <div className="p-4 border border-purple-500 rounded-lg flex-1">
      <h3 className="text-lg font-semibold mb-2">카운터 1</h3>
      <p className="mb-3">카운트: {count}</p>
      <button
        onClick={() => increment()}
        className="px-4 py-2 bg-purple-500 text-white rounded hover:bg-purple-600 transition-colors"
      >
        증가
      </button>
      <button
        onClick={() => decrement()}
        className="ml-2 px-4 py-2 bg-purple-500 text-white rounded hover:bg-purple-600 transition-colors"
      >
        감소
      </button>
      <button
        onClick={() => reset()}
        className="ml-2 px-4 py-2 bg-purple-500 text-white rounded hover:bg-purple-600 transition-colors"
      >
        리셋
      </button>
    </div>
  );
}
