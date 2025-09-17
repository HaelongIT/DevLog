import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";

export default function BoardList() {
  const [boards, setBoards] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchBoards = async () => {
      try {
        const response = await fetch("http://localhost:8080/api/boards");
        if (!response.ok) throw new Error("데이터를 불러오는데 실패했습니다.");
        const data = await response.json();
        setBoards(data);
      } catch (error) {
        console.error("게시글 목록 로딩 실패:", error);
      }
    };
    fetchBoards();
  }, []);

  return (
    <div className="card">
      <div className="card-header d-flex justify-content-between align-items-center">
        <span>게시글 목록</span>
        <Link to="/write" className="btn btn-primary btn-sm">
          <i className="bi bi-pencil-square me-1"></i>글쓰기
        </Link>
      </div>
      <div className="card-body">
        <table className="table table-hover">
          <thead className="table-light">
            <tr>
              <th scope="col" style={{ width: "10%" }}>
                #
              </th>
              <th scope="col" style={{ width: "60%" }}>
                제목
              </th>
              <th scope="col" style={{ width: "30%" }}>
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
