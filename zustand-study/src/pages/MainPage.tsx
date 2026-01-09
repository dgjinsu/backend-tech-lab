/**
 * 메인 페이지 - 각 예제로 이동할 수 있는 네비게이션
 */

import { useNavigate } from 'react-router-dom';

export function MainPage() {
  const navigate = useNavigate();

  return (
    <div className="p-5 max-w-[1200px] mx-auto">
      <header className="mb-8 text-center">
        <h1 className="text-4xl font-bold">🎓 React 전역 상태 관리 학습</h1>
        <p className="text-gray-600 mt-2 text-lg">
          Props Drilling부터 Jotai까지, 단계별로 알아보는 전역 상태 관리
        </p>
      </header>

      <div className="grid grid-cols-[repeat(auto-fit,minmax(280px,1fr))] gap-5 mt-10">
        {/* Props Drilling 카드 */}
        <div
          onClick={() => navigate('/props-drilling')}
          className="p-8 border-2 border-blue-500 rounded-xl cursor-pointer transition-all duration-300 bg-white shadow-md hover:-translate-y-1 hover:shadow-[0_4px_12px_rgba(52,152,219,0.3)]"
        >
          <h2 className="text-blue-500 mb-4 text-xl font-semibold">🔴 1. Props Drilling</h2>
          <p className="text-gray-600 leading-relaxed">
            가장 기본적인 방법으로 props를 통해 상태를 전달합니다.
            <br /><br />
            <strong>문제점:</strong> 중간 컴포넌트가 불필요한 props를 전달만 해야 하며,
            유지보수가 어려워집니다.
          </p>
        </div>

        {/* LocalStorage 카드 */}
        <div
          onClick={() => navigate('/local-storage')}
          className="p-8 border-2 border-orange-500 rounded-xl cursor-pointer transition-all duration-300 bg-white shadow-md hover:-translate-y-1 hover:shadow-[0_4px_12px_rgba(243,156,18,0.3)]"
        >
          <h2 className="text-orange-500 mb-4 text-xl font-semibold">🟡 2. LocalStorage</h2>
          <p className="text-gray-600 leading-relaxed">
            브라우저의 localStorage를 사용한 방법입니다.
            <br /><br />
            <strong>문제점:</strong> 동기적 작업으로 성능 저하, 자동 동기화 없음,
            타입 안정성 부족 등 여러 문제가 있습니다.
          </p>
        </div>

        {/* Zustand 카드 */}
        <div
          onClick={() => navigate('/zustand')}
          className="p-8 border-[3px] border-green-500 rounded-xl cursor-pointer transition-all duration-300 bg-green-50 shadow-md hover:-translate-y-1 hover:shadow-[0_4px_12px_rgba(46,204,113,0.4)]"
        >
          <h2 className="text-green-600 mb-4 text-xl font-semibold">🟢 4. Zustand ⭐</h2>
          <p className="text-gray-600 leading-relaxed">
            가볍고 간단한 전역 상태 관리 라이브러리입니다.
            <br /><br />
            <strong>장점:</strong> 간단한 API, 선택적 구독, Provider 불필요,
            완벽한 TypeScript 지원!
          </p>
        </div>

        {/* Jotai 카드 */}
        <div
          onClick={() => navigate('/jotai')}
          className="p-8 border-[3px] border-purple-500 rounded-xl cursor-pointer transition-all duration-300 bg-purple-50 shadow-md hover:-translate-y-1 hover:shadow-[0_4px_12px_rgba(155,89,182,0.4)]"
        >
          <h2 className="text-purple-600 mb-4 text-xl font-semibold">🟣 5. Jotai ⭐</h2>
          <p className="text-gray-600 leading-relaxed">
            Atom 기반의 가볍고 현대적인 상태 관리 라이브러리입니다.
            <br /><br />
            <strong>장점:</strong> Atom 단위 관리, Bottom-up 접근, 자동 최적화,
            완벽한 TypeScript 지원!
          </p>
        </div>
      </div>

      <footer className="mt-16 p-8 bg-gray-50 rounded-xl text-center">
        <h3 className="mb-4 text-xl font-semibold">💡 학습 가이드</h3>
        <p className="text-gray-600 text-base leading-relaxed">
          각 카드를 클릭하여 예제를 확인하세요!
          <br />
          순서대로 학습하면 전역 상태 관리의 발전 과정을 이해할 수 있습니다.
          <br /><br />
          <strong>추천 순서:</strong> Props Drilling → LocalStorage → Context API → Zustand → Jotai
        </p>
      </footer>
    </div>
  );
}
