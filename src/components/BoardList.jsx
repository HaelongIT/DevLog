import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";

const API_URL = "http://localhost:8080/api/boards";

export default function BoardList() {
  const [boards, setBoards] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    fetch(API_URL)
      .then((response) => response.json())
      .then((data) => setBoards(data))
      .catch((error) => console.error("Error fetching boards:", error));
  }, []);

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h1 className="h2">게시글 목록</h1>
        <button onClick={() => navigate("/write")} className="btn btn-primary">
          <i className="bi bi-pencil-fill me-2"></i>글쓰기
        </button>
      </div>
      <div className="table-responsive">
        <table className="table table-hover">
          <thead className="table-light">
            <tr>
              <th scope="col" style={{ width: "10%" }}>
                #
              </th>
              <th scope="col" style={{ width: "70%" }}>
                제목
              </th>
              <th scope="col" style={{ width: "20%" }}>
                작성자
              </th>
            </tr>
          </thead>
          <tbody>
            {boards.map((board) => (
              <tr
                key={board.id}
                onClick={() => navigate(`/board/${board.id}`)}
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
    </div>
  );
}
