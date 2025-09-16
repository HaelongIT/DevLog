import React, { useState, useEffect } from "react";

const API_URL = "http://localhost:8080/api/boards";

export default function BoardForm({ boardId, navigateTo }) {
  const isEditMode = boardId != null;
  const [form, setForm] = useState({ title: "", content: "", author: "" });

  useEffect(() => {
    if (isEditMode) {
      fetch(`${API_URL}/${boardId}`)
        .then((response) => response.json())
        .then((data) => setForm(data));
    } else {
      setForm({ title: "", content: "", author: "" });
    }
  }, [isEditMode, boardId]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prevForm) => ({ ...prevForm, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const url = isEditMode ? `${API_URL}/${boardId}` : API_URL;
    const method = isEditMode ? "PUT" : "POST";

    fetch(url, {
      method: method,
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(form),
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Network response was not ok");
        }
        return response.status === 204 ||
          response.headers.get("content-length") === "0"
          ? null
          : response.json();
      })
      .then((savedBoard) => {
        alert(`게시글이 성공적으로 ${isEditMode ? "수정" : "등록"}되었습니다.`);
        const targetId = isEditMode ? boardId : savedBoard.id;
        navigateTo("detail", targetId);
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
