package com.haelongit.devlog.user.dto.response;

import com.haelongit.devlog.user.entity.User;

import java.time.LocalDate;

public record UserResponseDto(String username, LocalDate paidUntil) {

    // User 엔티티를 이 DTO로 쉽게 변환하기 위한 생성자
    public UserResponseDto(User user) {
        this(user.getUsername(), user.getPaidUntil());
    }
}
