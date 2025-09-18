import React from "react";
import {
  Routes,
  Route,
  Navigate,
  useLocation,
  Outlet,
  useNavigate,
  Link,
} from "react-router-dom";
import { useAuth } from "./context/AuthContext.jsx";
import BoardList from "./components/BoardList.jsx";
import BoardDetail from "./components/BoardDetail.jsx";
import BoardForm from "./components/BoardForm.jsx";
import LoginPage from "./components/LoginPage.jsx";
import RegisterPage from "./components/RegisterPage.jsx"; // RegisterPage import 추가
import SubscriptionPage from "./components/SubscriptionPage.jsx"; // SubscriptionPage import

// 로그인한 사용자만 접근 가능한 경로를 보호하는 컴포넌트

// 1. 기존 ProtectedRoute는 로그인 여부만 확인 (변경 없음)
function ProtectedRoute() {
  const { isLoggedIn } = useAuth();

  const location = useLocation();

  if (!isLoggedIn) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  return <Outlet />;
}

// 2. 유료 구독자만 접근 가능한 경로를 보호하는 컴포넌트 (새로 추가)
function PaidUserRoute() {
  const { user } = useAuth();
  const location = useLocation();

  // user 객체에 paidUntil 정보가 있고, 만료일이 지나지 않았는지 확인
  const isSubscribed =
    user?.paidUntil && new Date(user.paidUntil) >= new Date();

  if (!isSubscribed) {
    // 구독하지 않은 사용자는 구독 페이지로 리다이렉트
    return <Navigate to="/subscribe" state={{ from: location }} replace />;
  }
  return <Outlet />;
}

// 게시판 페이지들의 공통 레이아웃 (헤더, 로그아웃 버튼 포함)

function Layout() {
  // logout과 함께 user 정보도 가져옵니다.
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    // AuthContext의 logout은 이제 비동기 함수입니다.
    await logout();
    navigate("/login");
  };

  return (
    <div className="container mt-5">
      <header className="d-flex justify-content-between align-items-center mb-4">
        <Link to="/boards" className="text-decoration-none text-dark">
          <h1>
            <i className="bi bi-card-text me-2"></i>게시판
          </h1>
        </Link>

        {/* 사용자 정보와 로그아웃 버튼을 함께 표시 */}
        <div className="d-flex align-items-center">
          <span className="me-3">
            <strong>{user?.username}</strong>님 환영합니다.
          </span>
          <button onClick={handleLogout} className="btn btn-outline-secondary">
            로그아웃
          </button>
        </div>
      </header>
      <main>
        <Outlet />
      </main>
    </div>
  );
}

export default function App() {
  const { isLoggedIn } = useAuth();

  return (
    <Routes>
      {/* 공개 경로 */}
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/" element={<Navigate to="/login" replace />} />

      {/* 3. 로그인한 사용자만 접근 가능한 경로 그룹 */}
      <Route element={<ProtectedRoute />}>
        {/* 구독 페이지는 로그인만 하면 접근 가능 */}
        <Route path="/subscribe" element={<SubscriptionPage />} />

        {/* 4. 유료 구독자만 접근 가능한 경로 그룹 */}
        <Route element={<PaidUserRoute />}>
          <Route element={<Layout />}>
            <Route path="/boards" element={<BoardList />} />
            <Route path="/board/:id" element={<BoardDetail />} />
            <Route path="/write" element={<BoardForm />} />
            <Route path="/edit/:id" element={<BoardForm />} />
          </Route>
        </Route>
      </Route>

      <Route
        path="*"
        element={<Navigate to={isLoggedIn ? "/boards" : "/login"} replace />}
      />
    </Routes>
  );
}
