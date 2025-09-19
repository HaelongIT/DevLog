// src/components/BoardDetail.jsx
import React, { useState, useEffect, useContext } from "react"; // useContext 추가
import { useParams, useNavigate, Link } from "react-router-dom";
import apiClient, {
  getComments,
  createComment,
  updateComment,
  deleteComment,
} from "../api"; // 댓글 API 함수들 import
import { useAuth } from "../context/AuthContext"; // AuthContext import

export default function BoardDetail() {
  const { id } = useParams(); // 게시글 ID
  const navigate = useNavigate();
  const { user } = useAuth(); // 로그인한 사용자 정보

  const [board, setBoard] = useState(null);
  const [comments, setComments] = useState([]); // 댓글 목록 상태
  const [newCommentContent, setNewCommentContent] = useState(""); // 새 댓글 내용 상태
  const [editingCommentId, setEditingCommentId] = useState(null); // 수정 중인 댓글 ID
  const [editingCommentContent, setEditingCommentContent] = useState(""); // 수정 중인 댓글 내용

  // 게시글 정보 불러오기
  useEffect(() => {
    const fetchBoard = async () => {
      try {
        const response = await apiClient.get(`/api/boards/${id}`);
        setBoard(response.data);
      } catch (error) {
        console.error("게시글 불러오기 실패:", error);
        alert("게시글을 불러올 수 없습니다.");
        navigate("/boards");
      }
    };
    fetchBoard();
  }, [id, navigate]);

  // 댓글 목록 불러오기
  const fetchComments = async () => {
    try {
      const response = await getComments(id); // 댓글 API 호출
      setComments(response.data);
    } catch (error) {
      console.error("댓글 불러오기 실패:", error);
      // alert("댓글을 불러올 수 없습니다."); // 사용자 경험을 위해 오류 메시지는 콘솔에만 표시
    }
  };

  useEffect(() => {
    fetchComments(); // 게시글 로드 후 댓글도 로드
  }, [id]); // id가 변경될 때마다 댓글 다시 불러오기

  // 게시글 삭제 핸들러
  const handleDeleteBoard = async () => {
    if (window.confirm("정말로 이 게시글을 삭제하시겠습니까?")) {
      try {
        await apiClient.delete(`/api/boards/${id}`);
        alert("게시글이 삭제되었습니다.");
        navigate("/boards");
      } catch (error) {
        console.error("게시글 삭제 실패:", error);
        alert("게시글 삭제에 실패했습니다.");
      }
    }
  };

  // 새 댓글 작성 핸들러
  const handleCreateComment = async () => {
    if (!user) {
      alert("로그인 후 댓글을 작성할 수 있습니다.");
      return;
    }
    if (!newCommentContent.trim()) {
      alert("댓글 내용을 입력해주세요.");
      return;
    }
    try {
      await createComment(id, { content: newCommentContent });
      setNewCommentContent(""); // 입력창 초기화
      fetchComments(); // 댓글 목록 새로고침
    } catch (error) {
      console.error("댓글 작성 실패:", error);
      alert("댓글 작성에 실패했습니다.");
    }
  };

  // 댓글 수정 모드 시작
  const handleEditCommentStart = (comment) => {
    setEditingCommentId(comment.id);
    setEditingCommentContent(comment.content);
  };

  // 댓글 수정 제출
  const handleUpdateComment = async (commentId) => {
    if (!editingCommentContent.trim()) {
      alert("수정할 댓글 내용을 입력해주세요.");
      return;
    }
    try {
      await updateComment(id, commentId, { content: editingCommentContent });
      setEditingCommentId(null); // 수정 모드 종료
      setEditingCommentContent(""); // 수정 내용 초기화
      fetchComments(); // 댓글 목록 새로고침
    } catch (error) {
      console.error("댓글 수정 실패:", error);
      alert("댓글 수정에 실패했거나 권한이 없습니다.");
    }
  };

  // 댓글 수정 취소
  const handleEditCommentCancel = () => {
    setEditingCommentId(null);
    setEditingCommentContent("");
  };

  // 댓글 삭제 핸들러
  const handleDeleteComment = async (commentId) => {
    if (window.confirm("정말로 이 댓글을 삭제하시겠습니까?")) {
      try {
        await deleteComment(id, commentId);
        alert("댓글이 삭제되었습니다.");
        fetchComments(); // 댓글 목록 새로고침
      } catch (error) {
        console.error("댓글 삭제 실패:", error);
        alert("댓글 삭제에 실패했거나 권한이 없습니다.");
      }
    }
  };

  if (!board) {
    return (
      <div className="d-flex justify-content-center">
        <div className="spinner-border" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="container mt-4">
      {/* 게시글 상세 부분 */}
      <div className="card mb-4">
        <div className="card-header">게시글 상세 정보</div>
        <div className="card-body">
          <h5 className="card-title">{board.title}</h5>
          <h6 className="card-subtitle mb-2 text-muted">
            작성자: {board.author}
          </h6>
          <p
            className="card-text"
            style={{ minHeight: "150px", whiteSpace: "pre-wrap" }}
          >
            {board.content}
          </p>
        </div>
        <div className="card-footer text-end">
          <Link to="/boards" className="btn btn-secondary me-2">
            목록으로
          </Link>
          {/* 게시글 작성자만 수정/삭제 버튼 보이도록 */}
          {user && user.username === board.author && (
            <>
              <Link to={`/edit/${id}`} className="btn btn-primary me-2">
                수정
              </Link>
              <button onClick={handleDeleteBoard} className="btn btn-danger">
                삭제
              </button>
            </>
          )}
        </div>
      </div>

      {/* 댓글 섹션 */}
      <div className="card mb-4">
        <div className="card-header">댓글</div>
        <div className="card-body">
          {/* 댓글 작성 폼 */}
          {user ? ( // 로그인한 사용자에게만 댓글 작성 폼 표시
            <div className="mb-3">
              <textarea
                className="form-control"
                rows="3"
                placeholder="댓글을 작성하세요..."
                value={newCommentContent}
                onChange={(e) => setNewCommentContent(e.target.value)}
              ></textarea>
              <button
                onClick={handleCreateComment}
                className="btn btn-primary mt-2"
              >
                댓글 작성
              </button>
            </div>
          ) : (
            <div className="alert alert-info" role="alert">
              로그인해야 댓글을 작성할 수 있습니다.
            </div>
          )}

          {/* 댓글 목록 */}
          <h6 className="mb-3">총 {comments.length}개의 댓글</h6>
          <ul className="list-group">
            {comments.map((comment) => (
              <li
                key={comment.id}
                className="list-group-item d-flex flex-column align-items-start"
              >
                <div className="d-flex w-100 justify-content-between">
                  <small className="text-muted">
                    <strong>{comment.authorUsername}</strong> -{" "}
                    {new Date(comment.createdAt).toLocaleString()}
                  </small>
                  {/* 댓글 작성자에게만 수정/삭제 버튼 표시 */}
                  {user && user.username === comment.authorUsername && (
                    <div>
                      {editingCommentId === comment.id ? (
                        <>
                          <button
                            onClick={() => handleUpdateComment(comment.id)}
                            className="btn btn-sm btn-success me-2"
                          >
                            저장
                          </button>
                          <button
                            onClick={handleEditCommentCancel}
                            className="btn btn-sm btn-secondary"
                          >
                            취소
                          </button>
                        </>
                      ) : (
                        <>
                          <button
                            onClick={() => handleEditCommentStart(comment)}
                            className="btn btn-sm btn-outline-primary me-2"
                          >
                            수정
                          </button>
                          <button
                            onClick={() => handleDeleteComment(comment.id)}
                            className="btn btn-sm btn-outline-danger"
                          >
                            삭제
                          </button>
                        </>
                      )}
                    </div>
                  )}
                </div>
                {editingCommentId === comment.id ? (
                  <textarea
                    className="form-control mt-2"
                    rows="2"
                    value={editingCommentContent}
                    onChange={(e) => setEditingCommentContent(e.target.value)}
                  ></textarea>
                ) : (
                  <p className="mb-1 mt-2" style={{ whiteSpace: "pre-wrap" }}>
                    {comment.content}
                  </p>
                )}
              </li>
            ))}
          </ul>
        </div>
      </div>
    </div>
  );
}
