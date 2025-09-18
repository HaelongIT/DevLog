import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios"; // axios import

// AuthContext와 동일한 axios 인스턴스 설정
const apiClient = axios.create({
  baseURL: "http://localhost:8080",
  withCredentials: true, // 세션 유지를 위한 필수 설정
});

export default function BoardForm() {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEditMode = Boolean(id);

  // author 필드를 초기 상태에서 제거합니다.
  const [board, setBoard] = useState({ title: "", content: "" });
  const [originalAuthor, setOriginalAuthor] = useState(""); // 수정 모드에서 작성자를 표시하기 위함

  useEffect(() => {
    if (isEditMode) {
      const fetchBoard = async () => {
        try {
          const response = await apiClient.get(`/api/boards/${id}`);
          const { title, content, author } = response.data;
          setBoard({ title, content });
          setOriginalAuthor(author); // 기존 작성자 정보 저장
        } catch (error) {
          console.error("게시글 정보를 가져오는데 실패했습니다.", error);
          alert("게시글 정보를 가져올 수 없습니다.");
          navigate("/boards");
        }
      };
      fetchBoard();
    }
  }, [id, isEditMode, navigate]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setBoard((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const url = isEditMode ? `/api/boards/${id}` : "/api/boards";
    const method = isEditMode ? "put" : "post";

    // 서버에는 제목과 내용만 보냅니다. 작성자는 서버가 세션에서 파악합니다.
    const payload = { title: board.title, content: board.content };

    try {
      const response = await apiClient[method](url, payload);
      alert(`게시글이 성공적으로 ${isEditMode ? "수정" : "등록"}되었습니다.`);

      // ===== 로직 수정 시작 =====
      if (isEditMode) {
        // 수정 모드일 경우, useParams에서 가져온 id를 사용해 상세 페이지로 이동
        navigate(`/board/${id}`);
      } else {
        // 생성 모드일 경우, 서버 응답에서 새로운 id를 가져와 상세 페이지로 이동
        const newBoardId = response.data.id;
        navigate(`/board/${newBoardId}`);
      }
      // ===== 로직 수정 끝 =====
    } catch (error) {
      console.error(error);
      if (error.response?.status === 403) {
        alert("이 게시글을 수정할 권한이 없습니다.");
      } else {
        alert(`데이터 ${isEditMode ? "수정" : "저장"}에 실패했습니다.`);
      }
    }
  };

  return (
    <div className="card">
      <div className="card-header">
        {isEditMode ? "게시글 수정" : "새 글 작성"}
      </div>
      <div className="card-body">
        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label htmlFor="title" className="form-label">
              제목
            </label>
            <input
              type="text"
              className="form-control"
              id="title"
              name="title"
              value={board.title}
              onChange={handleChange}
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
              name="content"
              rows="10"
              value={board.content}
              onChange={handleChange}
              required
            ></textarea>
          </div>

          {/* 수정 모드일 때만 작성자 정보를 표시합니다 (수정 불가) */}
          {isEditMode && (
            <div className="mb-3">
              <label className="form-label">작성자</label>
              <input
                type="text"
                className="form-control"
                value={originalAuthor}
                disabled
              />
            </div>
          )}

          <div className="text-end">
            <button
              type="button"
              onClick={() => navigate(-1)}
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
