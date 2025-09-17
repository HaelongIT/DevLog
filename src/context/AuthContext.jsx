import React, { createContext, useState, useContext } from "react";

const AuthContext = createContext(null);

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
  const [isLoggedIn, setIsLoggedIn] = useState(() => {
    return sessionStorage.getItem("isLoggedIn") === "true";
  });

  const login = (username, password) => {
    if (username === "admin" && password === "1234") {
      sessionStorage.setItem("isLoggedIn", "true");

      setIsLoggedIn(true);

      return true;
    }

    return false;
  };

  const logout = () => {
    sessionStorage.removeItem("isLoggedIn");

    setIsLoggedIn(false);
  };

  const value = { isLoggedIn, login, logout };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
