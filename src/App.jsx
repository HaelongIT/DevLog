import React from "react";
import { Routes, Route, Link } from "react-router-dom";
import BoardList from "./components/BoardList";
import BoardDetail from "./components/BoardDetail";
import BoardForm from "./components/BoardForm";

// 메인 앱 컴포넌트: 전체 레이아웃과 라우팅 규칙을 정의합니다.
export default function App() {
  return (
    <div className="container mt-5" style={{ maxWidth: "960px" }}>
      <header>
        <nav className="navbar navbar-expand-lg navbar-light bg-light mb-4 rounded shadow-sm">
          <div className="container-fluid">
            {/* <a> 태그 대신 <Link>를 사용해 페이지 새로고침 없이 이동합니다. */}
            <Link to="/" className="navbar-brand">
              <i className="bi bi-card-checklist me-2"></i>
              리액트 게시판
            </Link>
          </div>
        </nav>
      </header>
      <main>
        {/* URL 경로에 따라 어떤 컴포넌트를 보여줄지 정의하는 부분입니다. */}
        <Routes>
          <Route path="/" element={<BoardList />} />{" "}
          {/* 기본 경로(/)는 목록 페이지를 보여줍니다. */}
          <Route path="/board/:id" element={<BoardDetail />} />{" "}
          {/* /board/1 과 같은 경로는 상세 페이지를 보여줍니다. */}
          <Route path="/write" element={<BoardForm />} />{" "}
          {/* /write 경로는 작성 페이지를 보여줍니다. */}
          <Route path="/edit/:id" element={<BoardForm />} />{" "}
          {/* /edit/1 과 같은 경로는 수정 페이지를 보여줍니다. */}
        </Routes>
      </main>
      <footer className="text-center text-muted mt-5 py-3">
        <p>&copy; 2025 My React Board with Vite</p>
      </footer>
    </div>
  );
}
