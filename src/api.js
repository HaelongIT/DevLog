// src/api.js
import axios from "axios";

const apiClient = axios.create({
  baseURL: "http://localhost:8080",
  withCredentials: true, // 모든 요청에 쿠키를 포함
});

export default apiClient;
