package com.lshdainty.porest.login.controller;

import com.lshdainty.porest.common.controller.ApiResponse;
import com.lshdainty.porest.user.domain.User;
import com.lshdainty.porest.user.repository.UserRepositoryImpl;
import com.lshdainty.porest.lib.jwt.JwtUtil;
import com.lshdainty.porest.login.controller.dto.LoginDto;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.Objects;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class LoginApiController {
    private final UserRepositoryImpl userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * 토큰 재발급 API
     * Refresh Token을 이용해 새로운 Access Token 발급
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse> refreshToken(HttpServletRequest request) {
        String refreshTokenHeader = request.getHeader("Refresh-Token");

        if (refreshTokenHeader == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Refresh token not provided"));
        }

        try {
            // Refresh Token 검증
            if (jwtUtil.isExpired(refreshTokenHeader)) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.fail("Refresh token expired"));
            }

            String tokenType = jwtUtil.getTokenType(refreshTokenHeader);
            if (!"refresh".equals(tokenType)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.fail("Invalid token type"));
            }

            String userId = jwtUtil.getUserId(refreshTokenHeader);

            // 사용자 정보 조회
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.fail("User not found"));
            }

            User user = userOptional.get();

            // 새로운 Access Token 생성
            String newAccessToken = jwtUtil.generateAccessToken(userId, user.getRole().name());

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", newAccessToken);
            tokens.put("message", "Token refreshed successfully");

            return ResponseEntity.ok(ApiResponse.success(tokens));

        } catch (Exception e) {
            log.error("Token refresh failed", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.fail("Token refresh failed"));
        }
    }

    /**
     * 로그아웃 API
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout() {
        // JWT는 stateless이므로 서버에서 토큰을 무효화할 수 없음
        // 클라이언트에서 토큰을 삭제하도록 안내
        Map<String, String> result = new HashMap<>();
        result.put("message", "로그아웃 성공. 클라이언트에서 토큰을 삭제해주세요.");

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
}