package com.haelongit.devlog.payment.repository;

import com.haelongit.devlog.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // 기본 CRUD 메서드는 JpaRepository가 제공
    Optional<Payment> findByImpUid(String impUid);

    List<Payment> findByPaymentDateBetweenAndStatus(LocalDateTime startDate, LocalDateTime endDate, String status);

    // ===== 이 메서드 추가 =====
    /**
     * 주문번호(merchant_uid)로 결제 정보를 찾는 메서드
     * 웹훅 처리 시, PG사로부터 받은 주문번호를 사용해 기존 결제 데이터를 조회하는 데 사용됩니다.
     */
    Optional<Payment> findByMerchantUid(String merchantUid);
}
