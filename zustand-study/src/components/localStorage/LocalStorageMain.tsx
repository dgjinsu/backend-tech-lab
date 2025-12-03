/**
 * LocalStorage 메인 컴포넌트
 */

import { CounterComponent } from './CounterComponent';
import { AnotherCounterComponent } from './AnotherCounterComponent';
import { UserComponent } from './UserComponent';

export function LocalStorageMain() {
  return (
    <div className="p-5 border-2 border-yellow-500 rounded-lg">
      <h2 className="text-2xl font-bold mb-2">🟡 LocalStorage 방식</h2>
      <p className="text-yellow-700 mb-4">
        Props Drilling은 피하지만, 여러 문제점이 있습니다.
      </p>

      <div className="flex gap-5 mt-5">
        <CounterComponent />
        <AnotherCounterComponent />
      </div>

      <UserComponent />

      <div className="mt-5 p-4 bg-yellow-50 rounded">
        <strong className="text-base">⚠️ LocalStorage의 문제점:</strong>
        <ul className="mt-2 text-sm list-disc list-inside space-y-1">
          <li><strong>동기적 작업:</strong> localStorage.setItem()은 메인 스레드를 블로킹합니다</li>
          <li><strong>자동 동기화 없음:</strong> 한 컴포넌트에서 변경해도 다른 컴포넌트가 자동으로 알지 못함</li>
          <li><strong>이벤트 리스너 관리:</strong> storage 이벤트는 같은 탭에서는 작동하지 않음</li>
          <li><strong>타입 안정성 부족:</strong> 모든 값을 문자열로 저장하고 JSON.parse 필요</li>
          <li><strong>복잡한 상태 로직:</strong> derived state, computed values 구현이 복잡함</li>
          <li><strong>성능 문제:</strong> 큰 객체를 저장하면 직렬화/역직렬화 비용이 큼</li>
        </ul>
      </div>
    </div>
  );
}
