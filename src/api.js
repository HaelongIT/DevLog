// src/api.js
import axios from "axios";

const apiClient = axios.create({
  baseURL: "http://localhost:8080",
  withCredentials: true, // 모든 요청에 쿠키를 포함
});

// 게시글 API (기존 코드)
// export const getBoards = () => apiClient.get("/api/boards");
// export const getBoard = (id) => apiClient.get(`/api/boards/${id}`);
// export const createBoard = (boardData) => apiClient.post("/api/boards", boardData);
// export const updateBoard = (id, boardData) => apiClient.put(`/api/boards/${id}`, boardData);
// export const deleteBoard = (id) => apiClient.delete(`/api/boards/${id}`);

// 인증 API (기존 코드)
// export const register = (userData) => apiClient.post("/api/user/register", userData);
// export const login = (credentials) => apiClient.post("/api/user/login", credentials);
// export const logout = () => apiClient.post("/api/user/logout");

// 구독 API (기존 코드)
// export const preparePayment = (amount) => apiClient.post("/api/payment/prepare", { amount });
// export const processPayment = (paymentData) => apiClient.post("/api/payment/process", paymentData);
// export const extendSubscription = () => apiClient.post("/api/user/subscription/extend"); // 예시

// --- 새롭게 추가될 댓글 API ---
/**
 * 특정 게시글의 댓글 목록을 조회합니다.
 * GET /api/boards/{boardId}/comments
 * @param {number} boardId - 게시글 ID
 * @returns {Promise<Comment[]>} 댓글 목록
 */
export const getComments = (boardId) =>
  apiClient.get(`/api/boards/${boardId}/comments`);

/**
 * 특정 게시글에 새 댓글을 작성합니다.
 * POST /api/boards/{boardId}/comments
 * @param {number} boardId - 게시글 ID
 * @param {object} commentData - 댓글 내용 { content: string }
 * @returns {Promise<Comment>} 생성된 댓글 정보
 */
export const createComment = (boardId, commentData) =>
  apiClient.post(`/api/boards/${boardId}/comments`, commentData);

/**
 * 특정 댓글을 수정합니다.
 * PUT /api/boards/{boardId}/comments/{commentId}
 * @param {number} boardId - 게시글 ID (URL 일관성을 위해 필요하지만, 백엔드 로직에 직접 사용되지는 않을 수 있음)
 * @param {number} commentId - 수정할 댓글 ID
 * @param {object} commentData - 수정할 댓글 내용 { content: string }
 * @returns {Promise<Comment>} 수정된 댓글 정보
 */
export const updateComment = (boardId, commentId, commentData) =>
  apiClient.put(`/api/boards/${boardId}/comments/${commentId}`, commentData);

/**
 * 특정 댓글을 삭제합니다.
 * DELETE /api/boards/{boardId}/comments/{commentId}
 * @param {number} boardId - 게시글 ID (URL 일관성을 위해 필요하지만, 백엔드 로직에 직접 사용되지는 않을 수 있음)
 * @param {number} commentId - 삭제할 댓글 ID
 * @returns {Promise<void>}
 */
export const deleteComment = (boardId, commentId) =>
  apiClient.delete(`/api/boards/${boardId}/comments/${commentId}`);

export default apiClient;
