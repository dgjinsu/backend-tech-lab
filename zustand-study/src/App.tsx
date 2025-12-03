import { Routes, Route, Navigate, useNavigate, useLocation } from 'react-router-dom';
import { MainPage } from './pages/MainPage';
import { PropDrillingMain } from './components/propDrilling/PropDrillingMain';
import { LocalStorageMain } from './components/localStorage/LocalStorageMain';
import { ZustandMain } from './components/zustand/ZustandMain';
import './App.css';

function App() {
  const navigate = useNavigate();
  const location = useLocation();

  // 예제 페이지인지 확인
  const isExamplePage = ['/props-drilling', '/local-storage', '/context-api', '/zustand'].includes(location.pathname);

  return (
    <div style={{ padding: '20px', maxWidth: '1200px', margin: '0 auto' }}>
      {/* 예제 페이지에서만 헤더와 홈 버튼 표시 */}
      {isExamplePage && (
        <>
          <header style={{ marginBottom: '30px', textAlign: 'center', position: 'relative' }}>
            <button
              onClick={() => navigate('/main')}
              style={{
                position: 'absolute',
                left: 0,
                top: '50%',
                transform: 'translateY(-50%)',
                padding: '10px 20px',
                backgroundColor: '#95a5a6',
                color: 'white',
                border: 'none',
                borderRadius: '5px',
                cursor: 'pointer',
                fontSize: '14px',
                fontWeight: 'bold'
              }}
              onMouseEnter={(e) => {
                e.currentTarget.style.backgroundColor = '#7f8c8d';
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.backgroundColor = '#95a5a6';
              }}
            >
              ← 홈으로
            </button>

            <h1>🎓 React 전역 상태 관리 학습</h1>
            <p style={{ color: '#666', marginTop: '10px' }}>
              Props Drilling부터 Zustand까지, 단계별로 알아보는 전역 상태 관리
            </p>
          </header>
        </>
      )}

      {/* 라우팅 */}
      <Routes>
        <Route path="/" element={<Navigate to="/main" replace />} />
        <Route path="/main" element={<MainPage />} />
        <Route path="/props-drilling" element={<PropDrillingMain />} />
        <Route path="/local-storage" element={<LocalStorageMain />} />
        <Route path="/zustand" element={<ZustandMain />} />
      </Routes>

      {/* 예제 페이지에서만 푸터 표시 */}
      {isExamplePage && (
        <footer style={{
          marginTop: '40px',
          padding: '20px',
          backgroundColor: '#f8f9fa',
          borderRadius: '8px',
          textAlign: 'center'
        }}>
          <h3>💡 학습 팁</h3>
          <p style={{ color: '#666', fontSize: '14px' }}>
            개발자 도구의 콘솔을 열어서 각 예제의 리렌더링 패턴을 확인해보세요!
            <br />
            버튼을 클릭할 때마다 어떤 컴포넌트가 리렌더링되는지 로그로 확인할 수 있습니다.
          </p>
        </footer>
      )}
    </div>
  );
}

export default App;
