package com.haelongit.devlog.payment.repository;

import com.haelongit.devlog.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // 기본 CRUD 메서드는 JpaRepository가 제공
    Optional<Payment> findByImpUid(String impUid);
}
