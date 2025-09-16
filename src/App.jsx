import React, { useState, useEffect } from "react";

// API 기본 URL
const API_URL = "http://localhost:8080/api/boards";

// --- 컴포넌트 정의 ---

// 게시글 목록 컴포넌트
function BoardList({ navigateTo }) {
  const [boards, setBoards] = useState([]);

  useEffect(() => {
    fetch(API_URL)
      .then((response) => response.json())
      .then((data) => setBoards(data))
      .catch((error) =>
        console.error("게시글 목록을 불러오는 중 오류 발생:", error)
      );
  }, []);

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h1 className="h2">게시글 목록</h1>
        <button onClick={() => navigateTo("write")} className="btn btn-primary">
          <i className="bi bi-pencil-fill me-2"></i>글쓰기
        </button>
      </div>
      <table className="table table-hover">
        <thead className="table-light">
          <tr>
            <th scope="col">#</th>
            <th scope="col">제목</th>
            <th scope="col">작성자</th>
          </tr>
        </thead>
        <tbody>
          {boards.map((board) => (
            <tr
              key={board.id}
              onClick={() => navigateTo("detail", board.id)}
              style={{ cursor: "pointer" }}
            >
              <th scope="row">{board.id}</th>
              <td>{board.title}</td>
              <td>{board.author}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

// 게시글 상세 보기 컴포넌트
function BoardDetail({ boardId, navigateTo }) {
  const [board, setBoard] = useState(null);

  useEffect(() => {
    fetch(`${API_URL}/${boardId}`)
      .then((response) => response.json())
      .then((data) => setBoard(data))
      .catch((error) =>
        console.error("게시글 상세 정보를 불러오는 중 오류 발생:", error)
      );
  }, [boardId]);

  const handleDelete = () => {
    if (window.confirm("정말로 이 게시글을 삭제하시겠습니까?")) {
      fetch(`${API_URL}/${boardId}`, { method: "DELETE" })
        .then((response) => {
          if (response.ok) {
            alert("게시글이 삭제되었습니다.");
            navigateTo("list");
          } else {
            alert("게시글 삭제에 실패했습니다.");
          }
        })
        .catch((error) => console.error("게시글 삭제 중 오류 발생:", error));
    }
  };

  if (!board) return <div>로딩 중...</div>;

  return (
    <div className="card shadow-sm">
      <div className="card-header bg-light d-flex justify-content-between align-items-center">
        <h2 className="h4 mb-0">{board.title}</h2>
        <small className="text-muted">작성자: {board.author}</small>
      </div>
      <div className="card-body">
        <p style={{ minHeight: "200px", whiteSpace: "pre-wrap" }}>
          {board.content}
        </p>
      </div>
      <div className="card-footer d-flex justify-content-end">
        <button
          onClick={() => navigateTo("list")}
          className="btn btn-secondary me-2"
        >
          목록으로
        </button>
        <button
          onClick={() => navigateTo("edit", board.id)}
          className="btn btn-warning me-2"
        >
          수정
        </button>
        <button onClick={handleDelete} className="btn btn-danger">
          삭제
        </button>
      </div>
    </div>
  );
}

// 게시글 작성 및 수정 폼 컴포넌트
function BoardForm({ boardId, navigateTo }) {
  const isEditMode = boardId != null;
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [author, setAuthor] = useState("");

  useEffect(() => {
    if (isEditMode) {
      fetch(`${API_URL}/${boardId}`)
        .then((response) => response.json())
        .then((data) => {
          setTitle(data.title);
          setContent(data.content);
          setAuthor(data.author);
        });
    }
  }, [isEditMode, boardId]);

  const handleSubmit = (e) => {
    e.preventDefault();
    const boardData = { title, content, author };

    const url = isEditMode ? `${API_URL}/${boardId}` : API_URL;
    const method = isEditMode ? "PUT" : "POST";

    fetch(url, {
      method: method,
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(boardData),
    }).then((response) => {
      if (response.ok) {
        alert(`게시글이 성공적으로 ${isEditMode ? "수정" : "등록"}되었습니다.`);
        navigateTo(isEditMode ? "detail" : "list", boardId);
      } else {
        alert(`게시글 ${isEditMode ? "수정" : "등록"}에 실패했습니다.`);
      }
    });
  };

  return (
    <div className="card shadow-sm">
      <div className="card-body">
        <h1 className="card-title h3 mb-4">
          {isEditMode ? "게시글 수정" : "새 게시글 작성"}
        </h1>
        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label htmlFor="author" className="form-label">
              작성자
            </label>
            <input
              type="text"
              className="form-control"
              id="author"
              value={author}
              onChange={(e) => setAuthor(e.target.value)}
              required
              disabled={isEditMode}
            />
          </div>
          <div className="mb-3">
            <label htmlFor="title" className="form-label">
              제목
            </label>
            <input
              type="text"
              className="form-control"
              id="title"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              required
            />
          </div>
          <div className="mb-3">
            <label htmlFor="content" className="form-label">
              내용
            </label>
            <textarea
              className="form-control"
              id="content"
              rows="10"
              value={content}
              onChange={(e) => setContent(e.target.value)}
              required
            ></textarea>
          </div>
          <div className="d-flex justify-content-end">
            <button
              type="button"
              onClick={() =>
                navigateTo(isEditMode ? "detail" : "list", boardId)
              }
              className="btn btn-secondary me-2"
            >
              취소
            </button>
            <button type="submit" className="btn btn-primary">
              저장
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

// --- 메인 앱 컴포넌트 ---
export default function App() {
  // 페이지 상태와 현재 게시글 ID를 관리
  const [page, setPage] = useState({ name: "list", boardId: null });

  // 페이지 이동 함수
  const navigateTo = (name, boardId = null) => {
    setPage({ name, boardId });
  };

  // 현재 페이지 상태에 따라 렌더링할 컴포넌트를 결정
  const renderPage = () => {
    switch (page.name) {
      case "list":
        return <BoardList navigateTo={navigateTo} />;
      case "detail":
        return <BoardDetail boardId={page.boardId} navigateTo={navigateTo} />;
      case "write":
        return <BoardForm navigateTo={navigateTo} />;
      case "edit":
        return <BoardForm boardId={page.boardId} navigateTo={navigateTo} />;
      default:
        return <BoardList navigateTo={navigateTo} />;
    }
  };

  return (
    <div className="container mt-5">
      <nav className="navbar navbar-expand-lg navbar-light bg-light mb-4 rounded shadow-sm">
        <div className="container-fluid">
          <a
            className="navbar-brand"
            href="#"
            onClick={() => navigateTo("list")}
          >
            <i className="bi bi-card-checklist me-2"></i>
            리액트 게시판
          </a>
        </div>
      </nav>
      <main>{renderPage()}</main>
      <footer className="text-center text-muted mt-5">
        <p>&copy; 2025 My React Board</p>
      </footer>
    </div>
  );
}
