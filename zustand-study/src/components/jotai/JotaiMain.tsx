/**
 * Jotai 메인 컴포넌트
 */

import { CounterDisplay } from './CounterDisplay';
import { OptimizedCounterDisplay } from './OptimizedCounterDisplay';
import { UserDisplay } from './UserDisplay';
import { UnrelatedComponent } from './UnrelatedComponent';
import { PersistedCounterExample } from './PersistedCounterExample';

export function JotaiMain() {
  return (
    <div className="p-5 border-2 border-purple-500 rounded-lg">
      <h2 className="text-2xl font-bold mb-2">🟣 Jotai 방식</h2>

      <div className="flex gap-5 mt-5">
        <CounterDisplay />
        <OptimizedCounterDisplay />
      </div>

      <UserDisplay />
      <UnrelatedComponent />
      <PersistedCounterExample />

      <div className="mt-5 p-4 bg-purple-50 rounded">
        <strong className="text-base">✅ Jotai의 장점:</strong>
        <ul className="mt-2 text-sm list-disc list-inside space-y-1">
          <li><strong>Atom 기반:</strong> 작은 단위로 상태를 분리하여 관리</li>
          <li><strong>Bottom-up 접근:</strong> 필요한 atom만 조합하여 사용</li>
          <li><strong>자동 최적화:</strong> atom 단위로 자동으로 구독 관리</li>
          <li><strong>Provider 선택적:</strong> 기본적으로 전역, 필요 시 Provider 사용</li>
          <li><strong>TypeScript 우선:</strong> 완벽한 타입 추론 제공</li>
          <li><strong>작은 크기:</strong> 약 3KB (gzipped)</li>
          <li><strong>React Suspense 지원:</strong> 비동기 처리가 간편</li>
        </ul>
      </div>
    </div>
  );
}
