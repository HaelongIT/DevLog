package com.haelongit.devlog.payment.service;

import com.haelongit.devlog.payment.entity.Payment;
import com.haelongit.devlog.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    /**
     * 새로운 결제 내역 저장
     *
     * @param payment 저장할 Payment 엔티티
     * @return 저장된 Payment 엔티티
     */
    @Transactional
    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }
}
