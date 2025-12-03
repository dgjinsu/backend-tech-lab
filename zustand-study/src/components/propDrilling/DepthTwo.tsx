/**
 * DepthTwo 컴포넌트 - count나 user를 사용하지 않지만 전달만 함
 */

import { DepthThree } from './DepthThree';

interface DepthTwoProps {
  count: number;
  setCount: (count: number) => void;
  user: { name: string; role: string };
  setUser: (user: { name: string; role: string }) => void;
}

export function DepthTwo({ count, setCount, user, setUser }: DepthTwoProps) {
  return (
    <div className="m-5 p-4 border border-dashed border-gray-500">
      <p>⚠️ <strong>DepthTwo</strong>: 여기도 마찬가지로 전달만 합니다</p>

      <DepthThree
        count={count}
        setCount={setCount}
        user={user}
        setUser={setUser}
      />
    </div>
  );
}
