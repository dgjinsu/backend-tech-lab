# React 전역 상태 관리 완벽 가이드

## 목차
1. [왜 전역 상태 관리가 필요한가?](#왜-전역-상태-관리가-필요한가)
2. [Props Drilling 문제](#props-drilling-문제)
3. [전역 상태 관리 라이브러리 비교](#전역-상태-관리-라이브러리-비교)
4. [각 라이브러리 상세 분석](#각-라이브러리-상세-분석)
5. [최근 트렌드](#최근-트렌드)
6. [어떤 것을 선택해야 할까?](#어떤-것을-선택해야-할까)

---

## 왜 전역 상태 관리가 필요한가?

React 애플리케이션이 커지면서 다음과 같은 문제들이 발생합니다:

### 1. Props Drilling (속성 내려꽂기)
컴포넌트 트리가 깊어질수록 props를 여러 단계에 걸쳐 전달해야 합니다.

```tsx
// 😰 Props Drilling의 고통
function App() {
  const [user, setUser] = useState({ name: '홍길동' });

  return <Layout user={user} setUser={setUser} />;
}

function Layout({ user, setUser }) {
  return <Sidebar user={user} setUser={setUser} />;
}

function Sidebar({ user, setUser }) {
  return <UserProfile user={user} setUser={setUser} />;
}

function UserProfile({ user, setUser }) {
  // 여기서 드디어 사용!
  return <div>{user.name}</div>;
}
```

### 2. 상태 공유의 어려움
서로 멀리 떨어진 컴포넌트끼리 상태를 공유하기 어렵습니다.

```tsx
// Header와 Footer가 같은 사용자 정보를 사용하려면?
<App>
  <Header /> {/* 사용자 정보 필요 */}
  <Main />
  <Footer /> {/* 사용자 정보 필요 */}
</App>
```

### 3. 코드 복잡도 증가
- 중간 컴포넌트들이 불필요한 props를 받아서 전달만 함
- 컴포넌트가 재사용하기 어려워짐
- 리팩토링이 힘들어짐

---

## Props Drilling 문제

### 문제점

```tsx
// 5단계를 거쳐 props를 전달해야 하는 경우
<App count={count}>
  <Layout count={count}>
    <Sidebar count={count}>
      <Menu count={count}>
        <MenuItem count={count}>
          {/* 여기서 사용 */}
        </MenuItem>
      </Menu>
    </Sidebar>
  </Layout>
</App>
```

**문제:**
- Layout, Sidebar, Menu는 count를 사용하지 않는데도 전달만 함
- count의 타입이나 이름이 바뀌면 모든 중간 컴포넌트 수정 필요
- 컴포넌트 재사용이 어려움

### 해결책
전역 상태 관리를 사용하면:

```tsx
// 어떤 컴포넌트에서든 바로 접근 가능
function MenuItem() {
  const count = useGlobalState(state => state.count);
  return <div>{count}</div>;
}
```

---

## 전역 상태 관리 라이브러리 비교

### 주요 라이브러리 개요

| 라이브러리 | 번들 크기 | 학습 곡선 | 보일러플레이트 | DevTools | 타입 지원 |
|-----------|----------|----------|---------------|----------|-----------|
| **Redux** | ~15KB | 높음 ⬆️ | 많음 📝📝📝 | ✅ 최고 | ✅ 우수 |
| **MobX** | ~16KB | 중간 ➡️ | 적음 📝 | ✅ 좋음 | ✅ 우수 |
| **Recoil** | ~14KB | 중간 ➡️ | 중간 📝📝 | ✅ 좋음 | ✅ 우수 |
| **Zustand** | ~1KB | 낮음 ⬇️ | 거의 없음 📝 | ✅ 좋음 | ✅ 우수 |
| **Jotai** | ~3KB | 낮음 ⬇️ | 적음 📝 | ✅ 보통 | ✅ 우수 |
| **Context API** | 0KB (내장) | 낮음 ⬇️ | 중간 📝📝 | ❌ 없음 | ✅ 우수 |

---

## 각 라이브러리 상세 분석

### 1. Redux (2015년 출시)

#### 특징
- **가장 오래되고 성숙한** 상태 관리 라이브러리
- Flux 아키텍처 기반
- 단방향 데이터 흐름
- 불변성(Immutability) 강제

#### 장점 ✅
- **생태계가 가장 풍부함**: 미들웨어, 플러그인, 도구가 많음
- **Redux DevTools**: 최고의 디버깅 경험 (Time Travel, State Diff)
- **Redux Toolkit**: 보일러플레이트를 크게 줄여줌
- **검증된 패턴**: 대규모 프로젝트에서 입증됨
- **방대한 자료**: 튜토리얼, 예제, 커뮤니티가 크고 활발함

#### 단점 ❌
- **보일러플레이트가 많음**: Action, Reducer, Type 등 작성할 코드가 많음
- **학습 곡선이 가파름**: 개념 이해에 시간 필요
- **번들 크기가 큼**: ~15KB (Redux Toolkit 포함 시 더 큼)
- **비동기 처리 복잡**: Redux-Thunk, Redux-Saga 등 추가 학습 필요

#### 코드 예시
```tsx
// Redux Toolkit 사용 (현대적인 Redux)
import { createSlice, configureStore } from '@reduxjs/toolkit';

// Slice 정의
const counterSlice = createSlice({
  name: 'counter',
  initialState: { value: 0 },
  reducers: {
    increment: (state) => {
      state.value += 1; // Immer 덕분에 불변성 자동 처리
    },
    decrement: (state) => {
      state.value -= 1;
    }
  }
});

// Store 생성
const store = configureStore({
  reducer: {
    counter: counterSlice.reducer
  }
});

// 컴포넌트에서 사용
function Counter() {
  const count = useSelector((state) => state.counter.value);
  const dispatch = useDispatch();

  return (
    <button onClick={() => dispatch(counterSlice.actions.increment())}>
      {count}
    </button>
  );
}
```

#### 언제 사용?
- 대규모 엔터프라이즈 프로젝트
- 복잡한 상태 로직과 비즈니스 규칙
- 팀원들이 Redux에 익숙한 경우
- 강력한 디버깅 도구가 필요한 경우

---

### 2. MobX (2015년 출시)

#### 특징
- **반응형(Reactive) 프로그래밍** 기반
- 관찰 가능한(Observable) 상태
- 자동 추적 및 업데이트
- 객체 지향적 접근

#### 장점 ✅
- **직관적**: 일반 JavaScript 객체처럼 사용
- **보일러플레이트 적음**: Redux보다 훨씬 간결
- **자동 최적화**: 필요한 컴포넌트만 자동으로 리렌더링
- **유연함**: 다양한 패턴 사용 가능

#### 단점 ❌
- **마법 같은 동작**: 내부 동작 원리 이해가 어려울 수 있음
- **데코레이터 사용**: 추가 설정 필요 (선택사항)
- **디버깅 어려움**: Redux보다 상태 추적이 어려울 수 있음
- **번들 크기**: 약 16KB

#### 코드 예시
```tsx
import { makeObservable, observable, action } from 'mobx';
import { observer } from 'mobx-react-lite';

// Store 클래스
class CounterStore {
  count = 0;

  constructor() {
    makeObservable(this, {
      count: observable,
      increment: action,
      decrement: action
    });
  }

  increment() {
    this.count += 1;
  }

  decrement() {
    this.count -= 1;
  }
}

const counterStore = new CounterStore();

// 컴포넌트에서 사용
const Counter = observer(() => {
  return (
    <button onClick={() => counterStore.increment()}>
      {counterStore.count}
    </button>
  );
});
```

#### 언제 사용?
- 객체 지향 프로그래밍을 선호하는 경우
- 복잡한 도메인 모델이 있는 경우
- 빠른 프로토타이핑이 필요한 경우

---

### 3. Recoil (2020년 출시, Meta/Facebook)

#### 특징
- **Atom 기반**: 작은 단위의 상태 조각
- **Selector**: 파생된 상태 계산
- React 전용으로 설계됨
- Concurrent Mode 지원

#### 장점 ✅
- **React와 완벽한 통합**: Hooks처럼 자연스러움
- **선택적 구독**: Atom 단위로 정밀한 구독
- **비동기 지원**: Async Selector로 간단한 비동기 처리
- **코드 분할**: Atom을 필요할 때 로드 가능

#### 단점 ❌
- **아직 실험적**: 1.0 미만 버전 (안정성 우려)
- **Meta 내부 사용 중심**: 커뮤니티가 작음
- **문서 부족**: 예제와 패턴이 제한적
- **번들 크기**: 약 14KB

#### 코드 예시
```tsx
import { atom, selector, useRecoilState, useRecoilValue } from 'recoil';

// Atom 정의
const countState = atom({
  key: 'countState',
  default: 0
});

// Selector (파생 상태)
const doubleCountState = selector({
  key: 'doubleCountState',
  get: ({ get }) => {
    const count = get(countState);
    return count * 2;
  }
});

// 컴포넌트에서 사용
function Counter() {
  const [count, setCount] = useRecoilState(countState);
  const doubleCount = useRecoilValue(doubleCountState);

  return (
    <div>
      <button onClick={() => setCount(count + 1)}>{count}</button>
      <div>Double: {doubleCount}</div>
    </div>
  );
}
```

#### 언제 사용?
- React 18+ Concurrent 기능 활용
- 분산된 상태 관리 선호
- Meta 생태계를 따르는 경우

---

### 4. Zustand (2019년 출시) ⭐ **추천!**

#### 특징
- **극도로 단순함**: 최소한의 API
- **보일러플레이트 없음**: create 함수 하나로 끝
- **매우 작음**: 약 1KB (gzipped)
- React 외부에서도 사용 가능

#### 장점 ✅
- **학습 곡선이 거의 없음**: 5분이면 배움
- **번들 크기 최소**: 1KB로 성능 영향 거의 없음
- **선택적 구독**: 필요한 상태만 구독 가능
- **미들웨어 지원**: persist, devtools, immer 등
- **TypeScript 완벽 지원**: 타입 추론이 훌륭함
- **Provider 불필요**: 어디서든 바로 사용

#### 단점 ❌
- **생태계가 작음**: Redux만큼 많은 플러그인은 없음
- **복잡한 상태 로직**: 매우 복잡한 경우 구조화가 어려울 수 있음
- **DevTools가 약함**: Redux DevTools만큼 강력하지 않음

#### 코드 예시
```tsx
import { create } from 'zustand';
import { persist } from 'zustand/middleware';

// Store 생성 - 이게 끝!
const useStore = create((set) => ({
  count: 0,
  increment: () => set((state) => ({ count: state.count + 1 })),
  decrement: () => set((state) => ({ count: state.count - 1 })),

  // 객체도 쉽게
  user: { name: '홍길동', role: 'user' },
  toggleRole: () => set((state) => ({
    user: {
      ...state.user,
      role: state.user.role === 'user' ? 'admin' : 'user'
    }
  }))
}));

// Persist 미들웨어 사용
const usePersistedStore = create(
  persist(
    (set) => ({
      count: 0,
      increment: () => set((s) => ({ count: s.count + 1 }))
    }),
    { name: 'my-storage' }
  )
);

// 컴포넌트에서 사용
function Counter() {
  // 전체 상태 구독
  const { count, increment } = useStore();

  // 또는 특정 상태만 구독 (성능 최적화)
  const count = useStore((state) => state.count);

  return <button onClick={increment}>{count}</button>;
}
```

#### 언제 사용? ⭐
- 중소규모 프로젝트 (가장 추천!)
- 빠른 개발이 필요한 경우
- 번들 크기를 최소화하고 싶은 경우
- 간단하고 직관적인 API를 원하는 경우

---

### 5. Jotai (2020년 출시)

#### 특징
- **Atom 기반**: Recoil과 유사하지만 더 단순
- **Bottom-up 접근**: 작은 Atom을 조합
- 매우 가벼움 (~3KB)
- React Hooks 스타일

#### 장점 ✅
- **매우 가벼움**: 3KB로 Zustand 다음으로 작음
- **단순함**: Recoil보다 간단한 API
- **TypeScript 우선**: 타입 안정성이 뛰어남
- **SSR 지원**: Next.js와 궁합이 좋음

#### 단점 ❌
- **커뮤니티 작음**: 아직 성장 중
- **복잡한 상태**: 여러 Atom 관리가 복잡할 수 있음
- **DevTools 부족**: 디버깅 도구가 제한적

#### 코드 예시
```tsx
import { atom, useAtom } from 'jotai';

// Atom 정의
const countAtom = atom(0);
const doubleCountAtom = atom((get) => get(countAtom) * 2);

// 컴포넌트에서 사용
function Counter() {
  const [count, setCount] = useAtom(countAtom);
  const [doubleCount] = useAtom(doubleCountAtom);

  return (
    <div>
      <button onClick={() => setCount(count + 1)}>{count}</button>
      <div>Double: {doubleCount}</div>
    </div>
  );
}
```

#### 언제 사용?
- 작은 프로젝트
- Next.js 프로젝트
- 분산된 상태 관리 선호

---

### 6. Context API (React 내장)

#### 특징
- **React 내장**: 별도 설치 불필요
- **Provider 패턴**: Context.Provider로 감싸기
- 전역 상태보다는 "Props Drilling 해결용"

#### 장점 ✅
- **추가 의존성 없음**: React만 있으면 됨
- **학습 곡선 낮음**: React 개발자라면 이미 알고 있음
- **간단한 사용**: 소규모 상태에 적합

#### 단점 ❌
- **성능 문제**: Context 값이 변경되면 모든 Consumer 리렌더링
- **선택적 구독 불가**: 특정 필드만 구독 어려움
- **보일러플레이트**: Provider, Context, Custom Hook 작성 필요
- **Provider Hell**: 여러 Context 사용 시 중첩이 심해짐
- **DevTools 없음**: 상태 추적 어려움

#### 코드 예시
```tsx
import { createContext, useContext, useState } from 'react';

// Context 생성
const CounterContext = createContext(undefined);

// Provider 컴포넌트
function CounterProvider({ children }) {
  const [count, setCount] = useState(0);
  const increment = () => setCount(c => c + 1);

  return (
    <CounterContext.Provider value={{ count, increment }}>
      {children}
    </CounterContext.Provider>
  );
}

// Custom Hook
function useCounter() {
  const context = useContext(CounterContext);
  if (!context) throw new Error('useCounter must be used within CounterProvider');
  return context;
}

// 사용
function App() {
  return (
    <CounterProvider>
      <Counter />
    </CounterProvider>
  );
}

function Counter() {
  const { count, increment } = useCounter();
  return <button onClick={increment}>{count}</button>;
}
```

#### 언제 사용?
- 외부 라이브러리를 피하고 싶은 경우
- 테마, 언어 설정 등 단순한 전역 값
- 상태 변경이 거의 없는 경우

---

## 최근 트렌드

### 2024년 인기 순위 (npm 다운로드 기준)

1. **Redux** - 월 1000만+ 다운로드
   - 여전히 가장 많이 사용됨
   - 레거시 프로젝트와 대기업에서 선호

2. **Zustand** - 월 300만+ 다운로드 📈
   - **가장 빠르게 성장 중**
   - 새 프로젝트에서 선택률 급증

3. **MobX** - 월 200만+ 다운로드
   - 안정적인 사용자 기반
   - 틈새 시장에서 강세

4. **Recoil** - 월 100만+ 다운로드
   - 성장 정체
   - Meta 내부 중심

5. **Jotai** - 월 80만+ 다운로드 📈
   - 꾸준히 성장 중
   - Next.js 커뮤니티에서 인기

### 왜 Zustand가 인기를 얻고 있나?

#### 1. **단순함의 승리**
```tsx
// Redux: 많은 코드 필요
// - createSlice
// - configureStore
// - Provider 설정
// - useSelector, useDispatch

// Zustand: 한 줄로 시작
const useStore = create((set) => ({ count: 0 }));
```

#### 2. **번들 크기 민감도 증가**
- 모바일 환경이 중요해지면서 번들 크기가 핵심 요소
- Zustand: 1KB vs Redux: 15KB
- Lighthouse 점수에 직접적인 영향

#### 3. **DX (Developer Experience) 중시**
- 빠른 프로토타이핑
- 적은 보일러플레이트
- 직관적인 API

#### 4. **성능 최적화가 기본**
```tsx
// 선택적 구독이 쉬움
const count = useStore(state => state.count); // count만 구독
const user = useStore(state => state.user);   // user만 구독
```

#### 5. **유연한 아키텍처**
- Provider 불필요
- React 외부에서도 사용 가능
- 테스트하기 쉬움

#### 6. **충분한 기능**
```tsx
// 미들웨어 지원
import { persist, devtools } from 'zustand/middleware';

const useStore = create(
  devtools(
    persist(
      (set) => ({ /* 상태 */ }),
      { name: 'my-store' }
    )
  )
);
```

### 마이그레이션 시나리오

#### Redux → Zustand
```tsx
// Before (Redux)
const counterSlice = createSlice({
  name: 'counter',
  initialState: { value: 0 },
  reducers: {
    increment: (state) => { state.value += 1 }
  }
});

// After (Zustand) - 훨씬 간단!
const useStore = create((set) => ({
  value: 0,
  increment: () => set((s) => ({ value: s.value + 1 }))
}));
```

## 실전 팁

### 1. Zustand 베스트 프랙티스

```tsx
// ✅ 좋은 예: Store 분리
// stores/userStore.ts
export const useUserStore = create((set) => ({
  user: null,
  login: (user) => set({ user }),
  logout: () => set({ user: null })
}));

// stores/cartStore.ts
export const useCartStore = create((set) => ({
  items: [],
  addItem: (item) => set((s) => ({ items: [...s.items, item] }))
}));

// ✅ 선택적 구독으로 성능 최적화
function UserName() {
  const userName = useUserStore(s => s.user?.name);
  return <div>{userName}</div>;
}

// ✅ Immer 미들웨어로 불변성 쉽게
import { immer } from 'zustand/middleware/immer';

const useStore = create(
  immer((set) => ({
    nested: { deep: { value: 0 } },
    increment: () => set((state) => {
      state.nested.deep.value += 1; // 직접 수정 가능!
    })
  }))
);
```

### 3. 일반적인 실수 피하기

```tsx
// ❌ 나쁜 예: Store에 너무 많은 것을 넣음
const useStore = create((set) => ({
  // UI 상태는 로컬 상태로!
  isModalOpen: false,
  currentTab: 'home',
  // ... 너무 많은 상태
}));

// ✅ 좋은 예: 전역이 필요한 것만
const useStore = create((set) => ({
  user: null,
  theme: 'light',
  // 진짜 전역 상태만
}));
```

---

## 결론

### 핵심 요약

1. **전역 상태 관리는 필수**: Props Drilling을 피하고 효율적인 상태 공유

2. **2024년 추천**:
   - 🥇 **Zustand** - 대부분의 프로젝트
   - 🥈 **Redux Toolkit** - 대규모/복잡한 프로젝트
   - 🥉 **Jotai** - Next.js + 분산 상태

3. **트렌드**: Zustand가 빠르게 성장 중
   - 단순함 + 작은 크기 + 충분한 기능

4. **선택 기준**:
   - 프로젝트 규모
   - 팀의 경험
   - 성능 요구사항
   - 번들 크기 제약