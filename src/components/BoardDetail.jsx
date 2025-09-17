import React, { useState, useEffect } from "react";
import { useParams, useNavigate, Link } from "react-router-dom";

export default function BoardDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [board, setBoard] = useState(null);

  useEffect(() => {
    const fetchBoard = async () => {
      try {
        const response = await fetch(`http://localhost:8080/api/boards/${id}`);
        if (!response.ok) throw new Error("게시글을 찾을 수 없습니다.");
        const data = await response.json();
        setBoard(data);
      } catch (error) {
        console.error(error);
        alert(error.message);
        navigate("/boards");
      }
    };
    fetchBoard();
  }, [id, navigate]);

  const handleDelete = async () => {
    if (window.confirm("정말로 이 게시글을 삭제하시겠습니까?")) {
      try {
        const response = await fetch(`http://localhost:8080/api/boards/${id}`, {
          method: "DELETE",
        });
        if (!response.ok) throw new Error("삭제에 실패했습니다.");
        alert("삭제되었습니다.");
        navigate("/boards");
      } catch (error) {
        console.error(error);
        alert(error.message);
      }
    }
  };

  if (!board) {
    return (
      <div className="d-flex justify-content-center">
        <div className="spinner-border" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="card">
      <div className="card-header">게시글 상세 정보</div>
      <div className="card-body">
        <h5 className="card-title">{board.title}</h5>
        <h6 className="card-subtitle mb-2 text-muted">
          작성자: {board.author}
        </h6>
        <p className="card-text" style={{ minHeight: "150px" }}>
          {board.content}
        </p>
      </div>
      <div className="card-footer text-end">
        <Link to="/boards" className="btn btn-secondary me-2">
          목록으로
        </Link>
        <Link to={`/edit/${id}`} className="btn btn-primary me-2">
          수정
        </Link>
        <button onClick={handleDelete} className="btn btn-danger">
          삭제
        </button>
      </div>
    </div>
  );
}
