import { Navigate, Route, Routes } from 'react-router-dom';
import { LoginPage } from './pages/LoginPage';
import { ExpensesPage } from './pages/ExpensesPage';
import { ProtectedRoute } from './auth/ProtectedRoute';

function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route
        path="/expenses"
        element={
          <ProtectedRoute>
            <ExpensesPage />
          </ProtectedRoute>
        }
      />
      <Route path="/" element={<Navigate to="/expenses" replace />} />
      <Route path="*" element={<Navigate to="/expenses" replace />} />
    </Routes>
  );
}

export default App;
