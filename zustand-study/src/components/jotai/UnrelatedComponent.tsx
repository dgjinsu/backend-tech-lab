/**
 * Jotai atom을 구독하지 않는 컴포넌트
 *
 * 어떤 atom도 구독하지 않으면 atom 변경에 영향받지 않음
 */

export function UnrelatedComponent() {
  console.log('Jotai UnrelatedComponent 렌더링');

  return (
    <div className="mt-5 p-4 border border-gray-300 rounded-lg bg-gray-50">
      <h3 className="text-lg font-semibold mb-2 text-gray-600">
        관련 없는 컴포넌트
      </h3>
      <p className="text-gray-500">
        이 컴포넌트는 어떤 atom도 구독하지 않습니다.
      </p>
      <p className="text-xs text-gray-400 mt-2">
        atom 변경 시 리렌더링되지 않음 (콘솔 확인)
      </p>
    </div>
  );
}
