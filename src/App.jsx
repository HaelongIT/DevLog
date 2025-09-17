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

// 로그인한 사용자만 접근 가능한 경로를 보호하는 컴포넌트

function ProtectedRoute() {
  const { isLoggedIn } = useAuth();

  const location = useLocation();

  if (!isLoggedIn) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  return <Outlet />;
}

// 게시판 페이지들의 공통 레이아웃 (헤더, 로그아웃 버튼 포함)

function Layout() {
  const { logout } = useAuth();

  const navigate = useNavigate();

  const handleLogout = () => {
    logout();

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

        <button onClick={handleLogout} className="btn btn-outline-secondary">
          로그아웃
        </button>
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
      <Route path="/login" element={<LoginPage />} />

      <Route path="/" element={<Navigate to="/login" replace />} />

      <Route element={<ProtectedRoute />}>
        <Route element={<Layout />}>
          <Route path="/boards" element={<BoardList />} />

          <Route path="/board/:id" element={<BoardDetail />} />

          <Route path="/write" element={<BoardForm />} />

          <Route path="/edit/:id" element={<BoardForm />} />
        </Route>
      </Route>

      <Route
        path="*"
        element={<Navigate to={isLoggedIn ? "/boards" : "/login"} replace />}
      />
    </Routes>
  );
}
