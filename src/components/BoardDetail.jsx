import React, { useState, useEffect } from "react";

const API_URL = "http://localhost:8080/api/boards";

export default function BoardDetail({ boardId, navigateTo }) {
  const [board, setBoard] = useState(null);

  useEffect(() => {
    if (boardId) {
      fetch(`${API_URL}/${boardId}`)
        .then((response) => response.json())
        .then((data) => setBoard(data))
        .catch((error) => console.error("Error fetching board detail:", error));
    }
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
        .catch((error) => console.error("Error deleting board:", error));
    }
  };

  if (!board) return <div className="text-center">로딩 중...</div>;

  return (
    <div className="card shadow-sm">
      <div className="card-header bg-light d-flex justify-content-between align-items-center flex-wrap">
        <h2 className="h4 mb-0 me-3">{board.title}</h2>
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
