import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";

const API_URL = "http://localhost:8080/api/boards";

export default function BoardForm() {
  const { id } = useParams();
  const navigate = useNavigate();

  const isEditMode = id != null;
  const [form, setForm] = useState({ title: "", content: "", author: "" });

  useEffect(() => {
    if (isEditMode) {
      fetch(`${API_URL}/${id}`)
        .then((response) => response.json())
        .then((data) => setForm(data));
    } else {
      setForm({ title: "", content: "", author: "" });
    }
  }, [isEditMode, id]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prevForm) => ({ ...prevForm, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const url = isEditMode ? `${API_URL}/${id}` : API_URL;
    const method = isEditMode ? "PUT" : "POST";

    fetch(url, {
      method: method,
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(form),
    })
      .then((response) => {
        if (!response.ok) throw new Error("Network response was not ok");
        // 생성(POST) 시에는 서버로부터 응답 본문을 받아야 합니다.
        if (!isEditMode) {
          return response.json();
        }
        // 수정(PUT) 시에는 응답 본문이 필요 없습니다.
        return null;
      })
      .then((savedBoard) => {
        // 이 변수는 생성 모드일 때만 유효한 값을 가집니다.
        alert(`게시글이 성공적으로 ${isEditMode ? "수정" : "등록"}되었습니다.`);

        // 수정 모드일 때는 기존 id를, 생성 모드일 때는 서버 응답 객체의 id를 사용합니다.
        // Optional Chaining (?.)을 사용하여 savedBoard가 null이거나 id가 없을 때 오류를 방지합니다.
        const targetId = isEditMode ? id : savedBoard?.id;

        if (targetId) {
          // targetId가 유효하면 해당 상세 페이지로 이동합니다.
          navigate(`/board/${targetId}`);
        } else {
          // targetId를 받지 못한 경우(서버 응답 문제 등), 목록 페이지로 안전하게 이동합니다.
          console.warn(
            "새 게시글의 ID를 응답으로 받지 못했습니다. 목록 페이지로 이동합니다."
          );
          navigate("/");
        }
      })
      .catch((error) => {
        console.error("Error submitting form:", error);
        alert(`게시글 ${isEditMode ? "수정" : "등록"}에 실패했습니다.`);
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
              name="author"
              value={form.author}
              onChange={handleChange}
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
              name="title"
              value={form.title}
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
              value={form.content}
              onChange={handleChange}
              required
            ></textarea>
          </div>
          <div className="d-flex justify-content-end">
            <button
              type="button"
              onClick={() => navigate(isEditMode ? `/board/${id}` : "/")}
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
