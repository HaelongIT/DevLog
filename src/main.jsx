import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter } from "react-router-dom"; // react-router-dom 임포트
import App from "./App.jsx";

ReactDOM.createRoot(document.getElementById("root")).render(
  <React.StrictMode>
    <BrowserRouter>
      {" "}
      {/* BrowserRouter로 App 컴포넌트를 감싸서 라우팅 기능을 활성화합니다. */}
      <App />
    </BrowserRouter>
  </React.StrictMode>
);
