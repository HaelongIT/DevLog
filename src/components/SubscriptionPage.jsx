// src/components/SubscriptionPage.jsx

import React, { useState, useEffect } from "react"; // useState import 추가
import apiClient from "../api";
import { useAuth } from "../context/AuthContext";

export default function SubscriptionPage() {
  const { user } = useAuth();
  // 아래 useState 선언이 빠져있었습니다.
  const [isLoading, setIsLoading] = useState(false);

  // 아임포트(iamport) 라이브러리 로딩
  useEffect(() => {
    const script = document.createElement("script");
    script.src = "https://cdn.iamport.kr/v1/iamport.js";
    script.async = true;
    document.head.appendChild(script);
    return () => {
      document.head.removeChild(script);
    };
  }, []);

  const handlePayment = async () => {
    try {
      const prepareResponse = await apiClient.post("/api/payment/prepare", {
        amount: 100, // 실제 결제 금액
      });

      const { merchantUid, amount } = prepareResponse.data;
      const { IMP } = window;
      IMP.init("imp28284406"); // 본인의 아임포트 가맹점 식별코드로 교체

      IMP.request_pay(
        {
          pg: "kakaopay", // 본인의 PG사에 맞게 수정
          pay_method: "card",
          merchant_uid: merchantUid,
          name: "30일 이용권",
          amount: amount,
          buyer_email: "test@test.com",
          buyer_name: user.username,
          buyer_tel: "010-1234-5678",
        },
        async (rsp) => {
          if (rsp.success) {
            setIsLoading(true);
            alert("결제에 성공했습니다! 구독 상태를 확인 중입니다...");

            const checkInterval = setInterval(async () => {
              try {
                const response = await apiClient.get("/api/user/me");
                const updatedUser = response.data;
                const isSubscribed =
                  updatedUser?.paidUntil &&
                  new Date(updatedUser.paidUntil) >= new Date();

                if (isSubscribed) {
                  clearInterval(checkInterval);
                  setIsLoading(false);
                  alert("구독이 활성화되었습니다. 게시판으로 이동합니다.");
                  window.location.replace("/boards");
                }
              } catch (error) {
                clearInterval(checkInterval);
                setIsLoading(false);
                alert(
                  "구독 상태 확인에 실패했습니다. 잠시 후 다시 시도해주세요."
                );
              }
            }, 2000);
          } else {
            alert(`결제에 실패했습니다. 에러: ${rsp.error_msg}`);
          }
        }
      );
    } catch (error) {
      console.error("결제 준비 중 오류 발생:", error);
      alert("결제를 처리하는 중에 문제가 발생했습니다.");
    }
  };

  return (
    <div className="container mt-5">
      <div className="card text-center">
        <div className="card-header">
          <h2>구독 플랜</h2>
        </div>
        <div className="card-body">
          <h5 className="card-title">30일 이용권</h5>
          <p className="card-text">
            모든 게시판 기능을 30일 동안 자유롭게 이용하세요.
          </p>
          <h3>100원</h3>
          <button
            onClick={handlePayment}
            className="btn btn-primary btn-lg"
            disabled={isLoading}
          >
            {isLoading ? "구독 상태 확인 중..." : "결제하고 모든 기능 이용하기"}
          </button>
        </div>
        <div className="card-footer text-muted">
          결제는 아임포트 테스트 모드로 진행됩니다.
        </div>
      </div>
    </div>
  );
}
