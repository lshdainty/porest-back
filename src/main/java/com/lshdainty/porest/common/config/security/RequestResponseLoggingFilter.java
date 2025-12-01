package com.lshdainty.porest.common.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lshdainty.porest.common.util.PorestIP;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 모든 HTTP 요청/응답에 대한 포괄적인 로깅을 수행하는 필터
 * - Trace ID (UUID) 생성 및 MDC 설정
 * - Request/Response Body 캡처
 * - 실행 시간 측정
 * - User ID, Client IP 수집
 * - JSON 형태로 구조화된 로그 출력
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private static final String TRACE_ID_KEY = "requestId";
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
            "/actuator/health",
            "/actuator/prometheus",
            "/favicon.ico"
    );

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 제외할 경로는 로깅 없이 통과
        if (shouldNotFilter(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Trace ID 생성 및 MDC 설정
        String traceId = generateTraceId();
        MDC.put(TRACE_ID_KEY, traceId);

        // Request/Response Body를 여러 번 읽을 수 있도록 래핑
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();

        try {
            // 다음 필터로 전달
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;

            // 로그 출력
            logRequestResponse(wrappedRequest, wrappedResponse, traceId, executionTime);

            // Response Body를 실제 응답으로 복사 (중요!)
            wrappedResponse.copyBodyToResponse();

            // MDC 정리
            MDC.clear();
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return EXCLUDED_PATHS.stream().anyMatch(path::startsWith);
    }

    /**
     * Trace ID 생성 (UUID 기반 8자리)
     */
    private String generateTraceId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * 요청/응답 정보를 JSON 형태로 로깅
     */
    private void logRequestResponse(ContentCachingRequestWrapper request,
                                     ContentCachingResponseWrapper response,
                                     String traceId,
                                     long executionTime) {
        try {
            Map<String, Object> logData = new LinkedHashMap<>();

            // Trace ID
            logData.put("traceId", traceId);

            // HTTP Method & URI
            logData.put("method", request.getMethod());
            logData.put("uri", request.getRequestURI());
            if (request.getQueryString() != null) {
                logData.put("queryString", request.getQueryString());
            }

            // Client IP
            logData.put("clientIp", PorestIP.getClientIp());

            // User ID (인증된 경우)
            String userId = getCurrentUserId();
            if (userId != null) {
                logData.put("userId", userId);
            }

            // Request Body
            String requestBody = getRequestBody(request);
            if (requestBody != null && !requestBody.isEmpty()) {
                logData.put("requestBody", requestBody);
            }

            // Response Status & Body
            logData.put("responseStatus", response.getStatus());
            String responseBody = getResponseBody(response);
            if (responseBody != null && !responseBody.isEmpty()) {
                logData.put("responseBody", responseBody);
            }

            // Execution Time
            logData.put("executionTimeMs", executionTime);

            // JSON 형태로 로그 출력
            String logJson = objectMapper.writeValueAsString(logData);
            log.info("HTTP Request/Response: {}", logJson);

        } catch (Exception e) {
            log.error("Failed to log request/response", e);
        }
    }

    /**
     * Request Body 추출
     */
    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();
        if (content.length > 0) {
            String body = new String(content, StandardCharsets.UTF_8);
            // 비밀번호 등 민감한 정보 마스킹 (선택적)
            return maskSensitiveData(body);
        }
        return null;
    }

    /**
     * Response Body 추출
     */
    private String getResponseBody(ContentCachingResponseWrapper response) {
        byte[] content = response.getContentAsByteArray();
        if (content.length > 0) {
            return new String(content, StandardCharsets.UTF_8);
        }
        return null;
    }

    /**
     * 현재 인증된 사용자 ID 추출
     */
    private String getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
                return authentication.getName();
            }
        } catch (Exception e) {
            log.debug("Failed to get current user", e);
        }
        return null;
    }

    /**
     * 민감한 정보 마스킹 (비밀번호, 토큰 등)
     */
    private String maskSensitiveData(String body) {
        // 간단한 예시: password 필드를 마스킹
        // 실제로는 더 정교한 로직이 필요할 수 있음
        if (body.contains("password") || body.contains("user_pw")) {
            body = body.replaceAll("(\"password\"\\s*:\\s*\")([^\"]+)(\")", "$1***$3")
                      .replaceAll("(\"user_pw\"\\s*:\\s*\")([^\"]+)(\")", "$1***$3")
                      .replaceAll("(&|\\?)password=([^&]+)", "$1password=***")
                      .replaceAll("(&|\\?)user_pw=([^&]+)", "$1user_pw=***");
        }
        return body;
    }
}
