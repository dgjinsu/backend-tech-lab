/**
 * DepthOne 컴포넌트 - count나 user를 사용하지 않지만 전달만 함
 */

import { DepthTwo } from './DepthTwo';

interface DepthOneProps {
  count: number;
  setCount: (count: number) => void;
  user: { name: string; role: string };
  setUser: (user: { name: string; role: string }) => void;
}

export function DepthOne({ count, setCount, user, setUser }: DepthOneProps) {
  return (
    <div className="m-5 p-4 border border-dashed border-gray-500">
      <p>⚠️ <strong>DepthOne</strong>: count와 user를 사용하지 않지만 전달만 합니다</p>

      <DepthTwo
        count={count}
        setCount={setCount}
        user={user}
        setUser={setUser}
      />
    </div>
  );
}
