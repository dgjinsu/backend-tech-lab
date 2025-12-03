# 전역 상태 관리를 사용하지 않는 이유

> 출처: [전역 상태 관리에 대한 단상 (stale-while-revalidate)](https://velog.io/@woohm402/no-global-state-manager)

## 핵심 주장

전역 상태 관리 도구(Redux, Zustand, Recoil 등)는 "prop drilling을 피한다"는 이유로 널리 사용되지만, 실제로는 더 큰 문제를 야기할 수 있습니다.

## 전역 상태 관리의 7가지 단점

### 1. 데이터 흐름의 불명확성
- 데이터를 제공하는 곳과 소비하는 곳의 연결이 끊어짐
- 코드 추적이 어려워 유지보수 난이도 증가

### 2. 컴포넌트 재사용성 저하
- 전역 상태에 의존하면 컴포넌트가 특정 상태 구조에 강하게 결합됨
- 다른 프로젝트나 다른 컨텍스트에서 재사용 불가능

### 3. 반복적인 타입 체크
```typescript
// 전역 상태: 100개 컴포넌트에서 각각 null 체크
function Header() {
  const user = useStore(s => s.user);
  return <div>{user?.name}</div>;  // null 체크 1
}

function Profile() {
  const user = useStore(s => s.user);
  return <div>{user?.email}</div>;  // null 체크 2
}

function Settings() {
  const user = useStore(s => s.user);
  return <div>{user?.role}</div>;  // null 체크 3
}

// Props: 부모 1곳에서만 null 체크
function App() {
  const user = useAuth();

  if (!user) return <Login />;  // null 체크 1번만!

  // 이제 user는 User 타입으로 보장됨
  return (
    <>
      <Header user={user} />   {/* 안전 */}
      <Profile user={user} />  {/* 안전 */}
      <Settings user={user} /> {/* 안전 */}
    </>
  );
}

function Header({ user }: { user: User }) {
  return <div>{user.name}</div>;  // null 체크 불필요
}
```

**핵심**: 전역 상태는 모든 컴포넌트에서 반복적으로 null 체크가 필요하지만, Props는 부모에서 한 번만 체크하면 됩니다. 이는 타입을 더 안전하게 만들고 실수를 줄입니다.

### 4. 핸들러 권한 과다
- 자식 컴포넌트가 필요 이상의 모든 액션에 접근 가능
- 의도하지 않은 상태 변경 가능성 증가

### 5. 상태 초기화의 복잡성
```typescript
// 페이지 이동 시 상태를 수동으로 리셋해야 함
useEffect(() => {
  return () => {
    resetState(); // cleanup 로직 필요
  };
}, []);
```

### 6. 버그 가능성 증가
- 모든 컴포넌트가 모든 상태에 접근 가능
- 예상치 못한 부작용(side effect) 발생 위험

### 7. 라이브러리 의존성
- 상태 관리 라이브러리 변경 시 대규모 리팩토링 필요
- 벤더 락인(Vendor Lock-in) 문제

## 대안: Props + 로컬 상태

### Props의 장점
```typescript
// 명시적인 데이터 흐름
function UserProfile({ user, onUpdate }: Props) {
  // 타입 안전성 확보
  // 재사용 가능
  // 필요한 기능만 전달
}
```

- ✅ 데이터 흐름이 명시적
- ✅ 컴포넌트 재사용성 향상
- ✅ TypeScript와 함께라면 타입 안전성 확보
- ✅ 필요한 기능만 전달 가능

### 로컬 상태(useState)의 장점
```typescript
function UserForm() {
  const [user, setUser] = useState<User>({ name: '', role: 'user' });
  // 컴포넌트 언마운트 시 자동으로 정리됨
}
```

- ✅ 컴포넌트 언마운트 시 자동으로 정리
- ✅ 추가 초기화 로직 불필요
- ✅ 상태의 범위가 명확