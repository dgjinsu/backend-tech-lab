import { NavLink } from 'react-router-dom';

const menuItems = [
  { path: '/dashboard', label: '대시보드' },
  { path: '/expenses', label: '지출 관리' },
  { path: '/salary', label: '급여 관리' },
  { path: '/budget', label: '예산 관리' },
];

export default function Sidebar() {
  return (
    <aside className="w-56 border-r border-gray-200 bg-gray-50 p-4">
      <nav className="flex flex-col gap-1">
        {menuItems.map((item) => (
          <NavLink
            key={item.path}
            to={item.path}
            className={({ isActive }) =>
              `rounded-lg px-4 py-2.5 text-sm font-medium transition-colors ${
                isActive
                  ? 'bg-indigo-100 text-indigo-700'
                  : 'text-gray-600 hover:bg-gray-100 hover:text-gray-900'
              }`
            }
          >
            {item.label}
          </NavLink>
        ))}
      </nav>
    </aside>
  );
}
