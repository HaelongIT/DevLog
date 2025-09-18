import React, { useState, useEffect } from "react"; // useEffect 추가
import { Link, useNavigate } from "react-router-dom";
import apiClient from "../api"; // API 클라이언트 import
import { useAuth } from "../context/AuthContext"; // useAuth 추가

export default function RegisterPage() {
  const navigate = useNavigate();
  const { isLoggedIn } = useAuth(); // isLoggedIn 가져오기
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (isLoggedIn) {
      navigate("/boards", { replace: true });
    }
  }, [isLoggedIn, navigate]);

  const handleRegister = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      await apiClient.post("/api/user/register", { username, password });
      alert("회원가입에 성공했습니다! 로그인 페이지로 이동합니다.");
      navigate("/login");
    } catch (err) {
      // 서버에서 보낸 에러 메시지를 표시
      setError(err.response?.data || "회원가입 중 오류가 발생했습니다.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container vh-100 d-flex justify-content-center align-items-center">
      <div className="card shadow-sm" style={{ width: "400px" }}>
        <div className="card-body p-5">
          <h1 className="card-title text-center mb-4">회원가입</h1>
          <form onSubmit={handleRegister}>
            {/* 아이디, 비밀번호 입력 필드는 LoginPage.jsx와 동일 */}
            <div className="mb-3">
              <label htmlFor="username" className="form-label">
                아이디
              </label>
              <input
                type="text"
                className="form-control"
                id="username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
                disabled={loading}
              />
            </div>
            <div className="mb-3">
              <label htmlFor="password" className="form-label">
                비밀번호
              </label>
              <input
                type="password"
                className="form-control"
                id="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                disabled={loading}
              />
            </div>

            {error && (
              <div className="alert alert-danger p-2" role="alert">
                {error}
              </div>
            )}

            <div className="d-grid">
              <button
                type="submit"
                className="btn btn-primary mt-3"
                disabled={loading}
              >
                {loading ? "가입 처리 중..." : "회원가입"}
              </button>
            </div>
          </form>
          <div className="text-center mt-4">
            <Link to="/login">이미 계정이 있으신가요? 로그인</Link>
          </div>
        </div>
      </div>
    </div>
  );
}
