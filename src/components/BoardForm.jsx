import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";

export default function BoardForm() {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEditMode = Boolean(id);
  const [board, setBoard] = useState({ title: "", content: "", author: "" });

  useEffect(() => {
    if (isEditMode) {
      const fetchBoard = async () => {
        try {
          const response = await fetch(
            `http://localhost:8080/api/boards/${id}`
          );
          if (!response.ok)
            throw new Error("게시글 정보를 가져오는데 실패했습니다.");
          const data = await response.json();
          setBoard({
            title: data.title,
            content: data.content,
            author: data.author,
          });
        } catch (error) {
          console.error(error);
          alert(error.message);
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
    const url = isEditMode
      ? `http://localhost:8080/api/boards/${id}`
      : "http://localhost:8080/api/boards";
    const method = isEditMode ? "PUT" : "POST";

    try {
      const response = await fetch(url, {
        method: method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(board),
      });

      if (!response.ok) {
        throw new Error(
          `데이터 ${isEditMode ? "수정" : "저장"}에 실패했습니다.`
        );
      }

      const savedBoard = isEditMode ? { id: id } : await response.json();

      alert(`게시글이 성공적으로 ${isEditMode ? "수정" : "등록"}되었습니다.`);

      const targetId = savedBoard?.id;

      if (targetId) {
        navigate(`/board/${targetId}`);
        // ======================= 수정된 부분 시작 =======================
      } else if (!isEditMode) {
        // 수정 모드가 아니고, ID를 받지 못한 경우 (API 응답 형식이 다를 수 있음)
        // 안전하게 목록 페이지로 이동합니다.
        console.warn(
          "새 게시글의 ID를 응답으로 받지 못했습니다. 목록 페이지로 이동합니다."
        );
        navigate("/boards");
      } else {
        // 수정 모드에서는 응답에 ID가 없을 수 있으므로 현재 ID를 사용합니다.
        navigate(`/board/${id}`);
      }
      // ======================= 수정된 부분 끝 =========================
    } catch (error) {
      console.error(error);
      alert(error.message);
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
          <div className="mb-3">
            <label htmlFor="author" className="form-label">
              작성자
            </label>
            <input
              type="text"
              className="form-control"
              id="author"
              name="author"
              value={board.author}
              onChange={handleChange}
              required
              disabled={isEditMode}
            />
          </div>
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
