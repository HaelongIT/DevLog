import React, { createContext, useState, useContext, useEffect } from "react";
import apiClient from "../api"; // 이제 공용 apiClient를 import해서 사용합니다.

const AuthContext = createContext(null);

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const checkLoginStatus = async () => {
      try {
        const response = await apiClient.get("/api/user/me");
        // 이제 response.data는 { username: '...', paidUntil: '...' } 객체입니다.
        setUser(response.data); // 객체 전체를 user 상태에 저장합니다.
      } catch (error) {
        console.log("Not authenticated");
        setUser(null);
      } finally {
        setLoading(false);
      }
    };
    checkLoginStatus();
  }, []);

  const login = async (username, password) => {
    try {
      const params = new URLSearchParams();
      params.append("username", username);
      params.append("password", password);

      await apiClient.post("/api/user/login", params);

      const response = await apiClient.get("/api/user/me");
      // 로그인 성공 후에도 전체 사용자 객체를 저장합니다.
      setUser(response.data);
      return true;
    } catch (error) {
      console.error("Login failed:", error);
      setUser(null);
      return false;
    }
  };

  const logout = async () => {
    try {
      await apiClient.post("/api/user/logout");
    } catch (error) {
      console.error("Logout failed:", error);
    } finally {
      setUser(null);
    }
  };

  const isLoggedIn = !!user;

  // ===== 1. isSubscribed 상태 추가 =====
  // user 객체가 존재하고, paidUntil 날짜가 오늘보다 이후인지 확인
  const isSubscribed =
    user?.paidUntil && new Date(user.paidUntil) >= new Date();

  if (loading) {
    return null;
  }

  // ===== 2. value 객체에 isSubscribed 추가 =====
  const value = { user, isLoggedIn, isSubscribed, login, logout };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
