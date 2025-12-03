/**
 * 최적화된 카운터 컴포넌트 - count만 선택적으로 구독
 */

import { memo } from 'react';
import { useStore } from '../../stores/useStore';

export const OptimizedCounterDisplay = memo(function OptimizedCounterDisplay() {
  // count만 구독 - increment/decrement가 변경되어도 리렌더링 안 됨!
  const count = useStore((state) => state.count);
  console.log('OptimizedCounterDisplay 렌더링 (count만 구독)');

  return (
    <div className="p-4 border border-green-500 rounded-lg flex-1">
      <h3 className="text-lg font-semibold mb-2">카운터 2 (최적화됨)</h3>
      <p className="mb-3">카운트: {count}</p>
      <p className="text-xs text-green-700 mt-2">
        ✅ count만 구독하여 불필요한 리렌더링 없음
      </p>
    </div>
  );
});
