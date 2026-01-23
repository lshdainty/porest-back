package com.lshdainty.porest.common.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 설정 Properties
 * SSO에서 발급한 JWT를 검증하기 위한 설정
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT 서명 검증을 위한 비밀키
     * SSO와 동일한 키를 사용해야 함
     */
    private String secret;
}
