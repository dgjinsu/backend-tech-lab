/**
 * DepthThree 컴포넌트 - 실제로 상태를 사용하는 최종 컴포넌트
 */

interface DepthThreeProps {
  count: number;
  setCount: (count: number) => void;
  user: { name: string; role: string };
  setUser: (user: { name: string; role: string }) => void;
}

export function DepthThree({ count, setCount, user, setUser }: DepthThreeProps) {
  return (
    <div className="m-5 p-4 border-2 border-green-500 rounded-lg bg-gray-200">
      <h3 className="font-bold">✅ <strong>DepthThree</strong>: 여기서 실제로 사용합니다!</h3>

      <div className="mt-2.5">
        <p>카운트: {count}</p>
        <button
          onClick={() => setCount(count + 1)}
          className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 transition"
        >
          증가
        </button>
        <button
          onClick={() => setCount(count - 1)}
          className="ml-2.5 px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600 transition"
        >
          감소
        </button>
      </div>

      <div className="mt-4">
        <p>사용자: {user.name} ({user.role})</p>
        <button
          onClick={() => setUser({ ...user, role: user.role === 'user' ? 'admin' : 'user' })}
          className="px-4 py-2 bg-purple-500 text-white rounded hover:bg-purple-600 transition"
        >
          역할 변경
        </button>
      </div>
    </div>
  );
}
