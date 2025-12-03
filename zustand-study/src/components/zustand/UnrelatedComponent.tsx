/**
 * Store를 사용하지 않는 컴포넌트
 */

import { memo } from 'react';

export const UnrelatedComponent = memo(function UnrelatedComponent() {
  console.log('UnrelatedComponent 렌더링 (Store 사용 안 함)');

  return (
    <div className="mt-5 p-4 border border-gray-500 rounded-lg">
      <h3 className="text-lg font-semibold mb-2">관련 없는 컴포넌트</h3>
      <p className="mb-2">이 컴포넌트는 Store를 사용하지 않습니다</p>
    </div>
  );
});
