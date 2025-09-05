package com.haelongit.devlog.payment.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PortOneRequestUrl {

    // 토큰 발행 URL
    ACCESS_TOKEN_URL("/users/getToken"),
    // 결제 취소 URL
    CANCEL_PAYMENT_URL("/payments/cancel"),
    // 전체 결제 내역 목록 URL
    CREATE_PAYMENT_URL("/payments/");

    private final String url;
}
