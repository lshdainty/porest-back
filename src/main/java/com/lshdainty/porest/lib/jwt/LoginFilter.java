package com.lshdainty.porest.lib.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lshdainty.porest.login.controller.dto.LoginDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.setFilterProcessesUrl("/api/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse resp) throws AuthenticationException {
        // 클라이언트 요청에서 (id, pw) 추출
        LoginDto loginDto = null;
        try {
            String requestBody = StreamUtils.copyToString(req.getInputStream(), StandardCharsets.UTF_8);
            loginDto = new ObjectMapper().readValue(requestBody, LoginDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String userId = loginDto.getId();
        String password = loginDto.getPw();

        log.info("Attempting to authenticate user {}", userId);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userId, password, null);

        return authenticationManager.authenticate(token);
    }

    // 로그인 성공 시 실행하는 메소드
    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse resp, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("Successfully authenticated user {}", authResult.getPrincipal());

        CustomUserDetails principal = (CustomUserDetails) authResult.getPrincipal();
        String userId = principal.getUserId();

        Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();
        Iterator<? extends GrantedAuthority> authoritiesIterator = authorities.iterator();
        GrantedAuthority authority = authoritiesIterator.next();

        String role = authority.getAuthority();

        // Access Token과 Refresh Token 생성
        String accessToken = jwtUtil.generateAccessToken(userId, role);
        String refreshToken = jwtUtil.generateRefreshToken(userId);

        // Response Header에 토큰 추가
        resp.addHeader("Authorization", "Bearer " + accessToken);
        resp.addHeader("Refresh-Token", refreshToken);

        // JSON 응답
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write("{\"message\":\"로그인 성공\",\"accessToken\":\"" + accessToken + "\",\"refreshToken\":\"" + refreshToken + "\"}");
    }

    // 로그인 실패 시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest req, HttpServletResponse resp, AuthenticationException failed) throws IOException, ServletException {
        log.info("Unsuccessful authentication: {}", failed.getMessage());

        resp.setStatus(401);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write("{\"message\":\"로그인 실패\",\"error\":\"" + failed.getMessage() + "\"}");
    }
}