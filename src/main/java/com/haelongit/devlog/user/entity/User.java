package com.haelongit.devlog.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    // 생성자 (초기화를 위함)
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
