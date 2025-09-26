package com.lshdainty.porest.login.controller;

import com.lshdainty.porest.common.controller.ApiResponse;
import com.lshdainty.porest.user.domain.User;
import com.lshdainty.porest.user.repository.UserRepositoryImpl;
import com.lshdainty.porest.login.service.dto.LoginDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LoginApiController {
    private final UserRepositoryImpl userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * 로그인 API
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginDto loginDto, HttpServletRequest request) {
        try {
            Optional<User> userOptional = userRepository.findById(loginDto.getId());

            if (userOptional.isEmpty()) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.fail("사용자를 찾을 수 없습니다."));
            }

            User user = userOptional.get();

            if (!passwordEncoder.matches(loginDto.getPw(), user.getPwd())) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.fail("비밀번호가 일치하지 않습니다."));
            }

            HttpSession session = request.getSession();
            session.setAttribute("userId", user.getId());
            session.setAttribute("userRole", user.getRole().name());

            Map<String, String> result = new HashMap<>();
            result.put("message", "로그인 성공");
            result.put("userId", user.getId());
            result.put("role", user.getRole().name());

            return ResponseEntity.ok(ApiResponse.success(result));

        } catch (Exception e) {
            log.error("Login failed", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.fail("로그인 처리 중 오류가 발생했습니다."));
        }
    }

    /**
     * 로그아웃 API
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        Map<String, String> result = new HashMap<>();
        result.put("message", "로그아웃 성공");

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 비밀번호 인코딩 유틸리티 API (개발/테스트용)
     */
    @PostMapping("/encode-password")
    public ResponseEntity<ApiResponse> encodePassword(@RequestBody LoginDto loginDto) {
        log.info("Password encoding request for user: {}", loginDto.getId());

        String encodedPassword = passwordEncoder.encode(loginDto.getPw());

        Map<String, String> result = new HashMap<>();
        result.put("originalPassword", loginDto.getPw());
        result.put("encodedPassword", encodedPassword);

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/check-session")
    public ResponseEntity<ApiResponse> checkSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("isLoggedIn", false);
            result.put("message", "로그인이 필요합니다.");
            return ResponseEntity.ok(ApiResponse.success(result));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("isLoggedIn", true);
        result.put("userId", session.getAttribute("userId"));
        result.put("userRole", session.getAttribute("userRole"));
        result.put("message", "로그인 상태입니다.");

        return ResponseEntity.ok(ApiResponse.success(result));
    }
}