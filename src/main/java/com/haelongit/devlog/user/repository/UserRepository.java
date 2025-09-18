package com.haelongit.devlog.user.repository;

import com.haelongit.devlog.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 사용자 이름(username)으로 사용자를 찾는 메서드
     * 로그인 시 사용자를 식별하는 데 사용됩니다.
     * @param username 찾고자 하는 사용자의 이름
     * @return Optional<User> 사용자가 존재하면 User 객체를, 아니면 빈 Optional을 반환합니다.
     */
    Optional<User> findByUsername(String username);
}
