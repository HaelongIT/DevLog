import React, { useState } from "react";

import { useNavigate, useLocation } from "react-router-dom";

import { useAuth } from "../context/AuthContext";

export default function LoginPage() {
  const navigate = useNavigate();

  const location = useLocation();

  const { login } = useAuth();

  const [username, setUsername] = useState("");

  const [password, setPassword] = useState("");

  const [error, setError] = useState("");

  const from = location.state?.from?.pathname || "/boards";

  const handleLogin = (e) => {
    e.preventDefault();

    setError("");

    if (login(username, password)) {
      navigate(from, { replace: true });
    } else {
      setError("아이디 또는 비밀번호가 올바르지 않습니다.");
    }
  };

  return (
    <div className="container vh-100 d-flex justify-content-center align-items-center">
      <div className="card shadow-sm" style={{ width: "400px" }}>
        <div className="card-body p-5">
          <h1 className="card-title text-center mb-4">로그인</h1>

          <form onSubmit={handleLogin}>
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
              />
            </div>

            {error && (
              <div className="alert alert-danger p-2" role="alert">
                {error}
              </div>
            )}

            <div className="d-grid">
              <button type="submit" className="btn btn-primary mt-3">
                로그인
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
