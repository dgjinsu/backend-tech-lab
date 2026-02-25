import Card from '../components/common/Card';

export default function DashboardPage() {
  return (
    <div>
      <h2 className="mb-6 text-2xl font-bold text-gray-900">대시보드</h2>
      <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
        <Card>
          <p className="text-sm text-gray-500">이번 달 수입</p>
          <p className="mt-1 text-2xl font-bold text-gray-900">-</p>
        </Card>
        <Card>
          <p className="text-sm text-gray-500">이번 달 지출</p>
          <p className="mt-1 text-2xl font-bold text-gray-900">-</p>
        </Card>
        <Card>
          <p className="text-sm text-gray-500">남은 용돈</p>
          <p className="mt-1 text-2xl font-bold text-gray-900">-</p>
        </Card>
      </div>
      <Card className="mt-6">
        <p className="text-center text-gray-400 py-12">카테고리별 지출 차트가 여기에 표시됩니다.</p>
      </Card>
    </div>
  );
}
