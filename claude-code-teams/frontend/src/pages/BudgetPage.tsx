import Card from '../components/common/Card';

export default function BudgetPage() {
  return (
    <div>
      <h2 className="mb-6 text-2xl font-bold text-gray-900">예산 관리</h2>
      <Card>
        <p className="text-center text-gray-400 py-12">예산 설정이 여기에 표시됩니다.</p>
      </Card>
    </div>
  );
}
