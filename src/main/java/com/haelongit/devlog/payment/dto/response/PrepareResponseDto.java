package com.haelongit.devlog.payment.dto.response;

import com.haelongit.devlog.payment.entity.Payment;

import java.math.BigDecimal;

public record PrepareResponseDto(String merchantUid, BigDecimal amount) {
    public PrepareResponseDto(Payment payment) {
        this(payment.getMerchantUid(), payment.getPaymentAmount());
    }
}
