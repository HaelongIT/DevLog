import React, { createContext, useState, useContext, useEffect } from "react";
import axios from "axios"; // axios를 사용하기 위해 import 합니다.

// Axios 인스턴스 생성: 모든 요청에 공통 설정을 적용합니다.
const apiClient = axios.create({
  baseURL: "http://localhost:8080", // 백엔드 서버 주소
  withCredentials: true, // 세션 쿠키를 주고받기 위한 필수 설정
});

const AuthContext = createContext(null);

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
  // 로그인 상태뿐만 아니라 사용자 정보도 함께 저장합니다.
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true); // 로딩 상태 추가

  // 앱이 처음 로드될 때, 서버에 로그인 상태를 확인합니다.
  useEffect(() => {
    const checkLoginStatus = async () => {
      try {
        // '/api/user/me' API를 호출하여 현재 로그인된 사용자 정보를 가져옵니다.
        const response = await apiClient.get("/api/user/me");
        setUser({ username: response.data }); // 응답(username)으로 사용자 상태 설정
      } catch (error) {
        // API 호출 실패 시 (401 Unauthorized 등), 로그인되지 않은 상태로 처리합니다.
        console.log("Not authenticated");
        setUser(null);
      } finally {
        setLoading(false); // 로딩 상태 종료
      }
    };
    checkLoginStatus();
  }, []);

  const login = async (username, password) => {
    try {
      // Spring Security의 formLogin은 URL-encoded 형식의 데이터를 기대합니다.
      const params = new URLSearchParams();
      params.append("username", username);
      params.append("password", password);

      // '/api/user/login'으로 POST 요청을 보냅니다.
      await apiClient.post("/api/user/login", params);

      // 로그인 성공 후, 다시 '/api/user/me'를 호출하여 사용자 정보를 가져옵니다.
      const response = await apiClient.get("/api/user/me");
      setUser({ username: response.data });
      return true;
    } catch (error) {
      console.error("Login failed:", error);
      setUser(null);
      return false;
    }
  };

  const logout = async () => {
    try {
      // 서버에 로그아웃 요청을 보내 세션을 무효화합니다.
      await apiClient.post("/api/user/logout");
    } catch (error) {
      console.error("Logout failed:", error);
    } finally {
      // 성공 여부와 관계없이 클라이언트의 상태를 초기화합니다.
      setUser(null);
    }
  };

  // user 객체가 있으면 true, 없으면 false
  const isLoggedIn = !!user;

  // 로딩 중일 때는 아무것도 렌더링하지 않아, 깜빡임 현상을 방지합니다.
  if (loading) {
    return null;
  }

  const value = { user, isLoggedIn, login, logout };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
