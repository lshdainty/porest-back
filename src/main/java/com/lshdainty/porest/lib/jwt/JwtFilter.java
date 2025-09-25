package com.lshdainty.porest.lib.jwt;

import com.lshdainty.porest.user.domain.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@AllArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain filterChain) throws ServletException, IOException {
        // request header check
        String authHeader = req.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(req, resp);
            return;
        }

        String token = authHeader.split(" ")[1];

        try {
            // 토큰 만료 시간 검증
            if (jwtUtil.isExpired(token)) {
                log.warn("JWT token has expired");
                filterChain.doFilter(req, resp);
                return;
            }

            // 토큰 타입 검증 (access 토큰만 허용)
            String tokenType = jwtUtil.getTokenType(token);
            if (!"access".equals(tokenType)) {
                log.warn("Invalid token type: {}", tokenType);
                filterChain.doFilter(req, resp);
                return;
            }

            // 사용자 정보 획득
            String userId = jwtUtil.getUserId(token);
            String role = jwtUtil.getRole(token);

            // 간단한 User 객체 생성 (인증용)
            User user = User.createUser(userId);

            // userDetails에 회원 정보 객체 담기
            CustomUserDetails customUserDetails = new CustomUserDetails(user);

            // 스프링 시큐리티 인증 토큰 생성
            Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

            // SecurityContext에 사용자 등록
            SecurityContextHolder.getContext().setAuthentication(authToken);

        } catch (Exception e) {
            log.error("JWT authentication failed", e);
        }

        filterChain.doFilter(req, resp);
    }
}