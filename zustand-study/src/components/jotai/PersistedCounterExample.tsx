/**
 * LocalStorage와 연동된 Jotai 예제
 */

import { useAtomValue, useSetAtom } from 'jotai';
import { persistedCountAtom, persistedIncrementAtom, persistedDecrementAtom } from '../../atoms/persistedCounterAtoms';

export function PersistedCounterExample() {
  const count = useAtomValue(persistedCountAtom);
  const increment = useSetAtom(persistedIncrementAtom);
  const decrement = useSetAtom(persistedDecrementAtom);

  return (
    <div className="mt-5 p-4 border-2 border-blue-500 rounded-lg bg-blue-50">
      <h3 className="text-lg font-semibold mb-2">🔵 LocalStorage 연동 (atomWithStorage)</h3>
      <p className="mb-3">카운트: {count}</p>
      <button
        onClick={increment}
        className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 transition-colors"
      >
        증가
      </button>
      <button
        onClick={decrement}
        className="ml-2 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 transition-colors"
      >
        감소
      </button>
      <p className="text-xs text-blue-700 mt-2">
        ✅ 페이지를 새로고침해도 상태가 유지됩니다
        <br />
        ✅ atomWithStorage로 타입 안정성과 자동 동기화 제공
      </p>
    </div>
  );
}
