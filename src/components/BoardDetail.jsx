// src/components/BoardDetail.jsx (수정 후)
import React, { useState, useEffect } from "react";
import { useParams, useNavigate, Link } from "react-router-dom";
import apiClient from "../api"; // apiClient import

export default function BoardDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [board, setBoard] = useState(null);

  useEffect(() => {
    const fetchBoard = async () => {
      try {
        // fetch -> apiClient.get으로 변경
        const response = await apiClient.get(`/api/boards/${id}`);
        setBoard(response.data);
      } catch (error) {
        console.error(error);
        alert("게시글을 불러올 수 없습니다.");
        navigate("/boards");
      }
    };
    fetchBoard();
  }, [id, navigate]);

  const handleDelete = async () => {
    if (window.confirm("정말로 이 게시글을 삭제하시겠습니까?")) {
      try {
        // fetch -> apiClient.delete로 변경
        await apiClient.delete(`/api/boards/${id}`);
        alert("삭제되었습니다.");
        navigate("/boards");
      } catch (error) {
        console.error(error);
        alert("삭제에 실패했습니다.");
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
