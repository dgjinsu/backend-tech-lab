/**
 * Zustand 메인 컴포넌트
 */

import { CounterDisplay } from './CounterDisplay';
import { OptimizedCounterDisplay } from './OptimizedCounterDisplay';
import { UserDisplay } from './UserDisplay';
import { UnrelatedComponent } from './UnrelatedComponent';
import { PersistedCounterExample } from './PersistedCounterExample';

export function ZustandMain() {
  return (
    <div className="p-5 border-2 border-green-500 rounded-lg">
      <h2 className="text-2xl font-bold mb-2">🟢 Zustand 방식</h2>

      <div className="flex gap-5 mt-5">
        <CounterDisplay />
        <OptimizedCounterDisplay />
      </div>

      <UserDisplay />
      <UnrelatedComponent />
      <PersistedCounterExample />

      <div className="mt-5 p-4 bg-green-50 rounded">
        <strong className="text-base">✅ Zustand의 장점:</strong>
        <ul className="mt-2 text-sm list-disc list-inside space-y-1">
          <li><strong>간단한 API:</strong> create 함수 하나로 스토어 생성</li>
          <li><strong>선택적 구독:</strong> 필요한 상태만 구독하여 리렌더링 최소화</li>
          <li><strong>Provider 불필요:</strong> 컴포넌트 어디서나 바로 사용</li>
          <li><strong>TypeScript 지원:</strong> 완벽한 타입 추론</li>
          <li><strong>미들웨어:</strong> persist, devtools, immer 등 제공</li>
          <li><strong>작은 크기:</strong> 약 1KB (gzipped)</li>
          <li><strong>React 독립적:</strong> React 외부에서도 사용 가능</li>
        </ul>
      </div>
    </div>
  );
}
