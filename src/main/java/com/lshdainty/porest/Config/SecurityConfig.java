package com.lshdainty.porest.Config;

import com.lshdainty.porest.lib.jwt.JwtFilter;
import com.lshdainty.porest.lib.jwt.JwtUtil;
import com.lshdainty.porest.lib.jwt.LoginFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
    private final AuthenticationConfiguration authConfig;
    private final JwtUtil jwtUtil;

    // LoginFilter에 필요한 인증 매니저 주입
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // password 암호화를 위한 bean 등록
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 비활성화
        http.csrf(AbstractHttpConfigurer::disable);

        // Form 로그인 방식 비활성화
        http.formLogin(AbstractHttpConfigurer::disable);

        // HTTP Basic 인증 방식 비활성화
        http.httpBasic(AbstractHttpConfigurer::disable);

        // 경로별 인가 작업
        http.authorizeHttpRequests((auth) -> auth
                // 인증 없이 접근 가능한 경로들
                .requestMatchers(
                        "/",
                        "/api/login",                    // 기존 로그인 (LoginFilter)
                        "/api/logout",                   // 로그아웃
                        "/api/refresh-token",            // 토큰 재발급
                        "/api/encode-password",          // 비밀번호 인코딩 (개발용)
                        "/api/v2/auth/**",              // 새로운 통합 인증 API
                        "/api/oauth/**",                // OAuth 관련
                        "/swagger-ui/**",               // Swagger UI
                        "/v3/api-docs/**"               // Swagger API 문서
                ).permitAll()
                // 그 외 모든 요청은 인증 필요
                .anyRequest().authenticated()
        );

        // JWT 검증 필터를 LoginFilter 앞에 추가
        http.addFilterBefore(new JwtFilter(jwtUtil), LoginFilter.class);

        // LoginFilter를 UsernamePasswordAuthenticationFilter 위치에 추가
        http.addFilterAt(
                new LoginFilter(authenticationManager(authConfig), jwtUtil),
                UsernamePasswordAuthenticationFilter.class
        );

        // 세션 정책: Stateless (JWT 사용)
        http.sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}