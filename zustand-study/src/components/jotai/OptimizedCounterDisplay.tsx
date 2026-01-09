/**
 * 최적화된 카운터 컴포넌트 - count만 선택적으로 구독
 */

import { memo } from 'react';
import { useAtomValue } from 'jotai';
import { countAtom } from '../../atoms/counterAtoms';

export const OptimizedCounterDisplay = memo(function OptimizedCounterDisplay() {
  // count만 구독 - 다른 atom이 변경되어도 리렌더링 안 됨!
  const count = useAtomValue(countAtom);
  console.log('OptimizedCounterDisplay 렌더링 (Jotai - count만 구독)');

  return (
    <div className="p-4 border border-purple-500 rounded-lg flex-1">
      <h3 className="text-lg font-semibold mb-2">카운터 2 (최적화됨)</h3>
      <p className="mb-3">카운트: {count}</p>
      <p className="text-xs text-purple-700 mt-2">
        ✅ countAtom만 구독하여 불필요한 리렌더링 없음
      </p>
    </div>
  );
});
