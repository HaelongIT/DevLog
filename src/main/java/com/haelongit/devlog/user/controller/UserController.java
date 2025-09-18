package com.haelongit.devlog.user.controller;

import com.haelongit.devlog.user.dto.UserDto;
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

    // 회원가입 API
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDto userDto) {
        try {
            userService.register(userDto.username(), userDto.password());
            return ResponseEntity.ok("Registration successful");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 현재 로그인된 사용자 정보 조회 API
    @GetMapping("/me")
    public ResponseEntity<String> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            // 사용자가 인증되지 않은 경우
            return ResponseEntity.status(401).body("Not authenticated");
        }
        // 인증된 사용자의 username 반환
        return ResponseEntity.ok(userDetails.getUsername());
    }
}
