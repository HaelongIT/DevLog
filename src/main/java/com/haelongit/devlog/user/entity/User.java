package com.haelongit.devlog.user.entity;

import com.haelongit.devlog.payment.entity.Payment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users") // 데이터베이스에서 'users' 테이블에 매핑됩니다.
@Getter
@Setter
@NoArgsConstructor // JPA는 기본 생성자를 필요로 합니다.
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id") // 컬럼명을 명시적으로 지정
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    // ===== 1. 만료일 필드 추가 =====
    @Column(name = "paid_until")
    private LocalDate paidUntil;

    // ===== 2. Payment와 연관관계 추가 =====
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();


    // 생성자 (초기화를 위함)
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // ===== 3. 구독 연장 편의 메서드 추가 =====
    public void extendSubscription(int days) {
        if (this.paidUntil == null || this.paidUntil.isBefore(LocalDate.now())) {
            // 구독 정보가 없거나 이미 만료된 경우, 오늘부터 기간 추가
            this.paidUntil = LocalDate.now().plusDays(days);
        } else {
            // 기존 구독 기간이 남아있는 경우, 만료일로부터 기간 추가
            this.paidUntil = this.paidUntil.plusDays(days);
        }
    }
}
