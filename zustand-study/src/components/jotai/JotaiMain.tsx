/**
 * Jotai 메인 컴포넌트
 */

import { CounterDisplay } from './CounterDisplay';
import { OptimizedCounterDisplay } from './OptimizedCounterDisplay';
import { UserDisplay } from './UserDisplay';
import { UnrelatedComponent } from './UnrelatedComponent';

export function JotaiMain() {
  return (
    <div className="p-5 border-2 border-purple-500 rounded-lg">
      <h2 className="text-2xl font-bold mb-2">Jotai 방식</h2>

      <div className="flex gap-5 mt-5">
        <CounterDisplay />
        <OptimizedCounterDisplay />
      </div>

      <UserDisplay />
      <UnrelatedComponent />

      <div className="mt-5 p-4 bg-purple-50 rounded">
        <strong className="text-base">Jotai의 장점:</strong>
        <ul className="mt-2 text-sm list-disc list-inside space-y-1">
          <li>
            <strong>Atomic 모델:</strong> 상태를 작은 atom 단위로 분리하여 관리
          </li>
          <li>
            <strong>파생 상태:</strong> derived atom으로 계산된 값 자동 갱신
          </li>
          <li>
            <strong>세밀한 구독:</strong> useAtomValue, useSetAtom으로 최적화
          </li>
          <li>
            <strong>Provider 선택적:</strong> 기본적으로 Provider 불필요
          </li>
          <li>
            <strong>Suspense 지원:</strong> 비동기 atom과 React Suspense 통합
          </li>
          <li>
            <strong>작은 크기:</strong> 약 2KB (gzipped)
          </li>
          <li>
            <strong>TypeScript:</strong> 완벽한 타입 추론 지원
          </li>
        </ul>
      </div>

      <div className="mt-5 p-4 bg-purple-100 rounded">
        <strong className="text-base">Zustand vs Jotai 비교:</strong>
        <ul className="mt-2 text-sm list-disc list-inside space-y-1">
          <li>
            <strong>Zustand:</strong> 단일 스토어, 객체 기반, Redux 스타일
          </li>
          <li>
            <strong>Jotai:</strong> 다중 atom, 원자적 상태, Recoil 스타일
          </li>
          <li>
            <strong>구독 방식:</strong> Zustand는 selector, Jotai는 atom 단위
          </li>
          <li>
            <strong>파생 상태:</strong> Zustand는 수동, Jotai는 derived atom
          </li>
        </ul>
      </div>
    </div>
  );
}
