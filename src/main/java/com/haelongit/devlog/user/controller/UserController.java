package com.haelongit.devlog.user.controller;

import com.haelongit.devlog.user.dto.request.UserRegisterRequestDto;
import com.haelongit.devlog.user.dto.response.UserResponseDto;
import com.haelongit.devlog.user.entity.User;
import com.haelongit.devlog.user.repository.UserRepository;
import com.haelongit.devlog.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository; // UserRepository 주입 추가


    // 회원가입 API
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegisterRequestDto userRegisterRequestDto) {
        try {
            userService.register(userRegisterRequestDto.username(), userRegisterRequestDto.password());
            return ResponseEntity.ok("Registration successful");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 현재 로그인된 사용자 정보 조회 API (응답 형식 변경)
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            // Spring Security 설정에 의해 이 코드는 사실상 도달하기 어렵지만, 안전장치로 둡니다.
            return ResponseEntity.status(401).build();
        }

        // 1. UserDetails에서 username을 가져옵니다.
        String username = userDetails.getUsername();

        // 2. UserRepository를 사용해 전체 User 엔티티 정보를 조회합니다.
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 3. 조회한 User 엔티티를 UserResponseDto로 변환하여 반환합니다.
        return ResponseEntity.ok(new UserResponseDto(user));
    }
}
