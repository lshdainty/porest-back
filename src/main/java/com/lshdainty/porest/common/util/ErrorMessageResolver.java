package com.lshdainty.porest.common.util;

import com.lshdainty.porest.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * ErrorCode의 메시지를 MessageSource에서 가져오는 유틸리티
 * 다국어 지원을 위해 현재 Locale에 맞는 메시지 반환
 */
@Component
@RequiredArgsConstructor
public class ErrorMessageResolver {

    private final MessageSource messageSource;

    /**
     * ErrorCode에서 메시지 가져오기 (현재 Locale 사용)
     */
    public String getMessage(ErrorCode errorCode) {
        return messageSource.getMessage(
                errorCode.getMessageKey(),
                null,
                errorCode.getCode(), // 메시지 키를 못 찾을 경우 기본값으로 코드 반환
                LocaleContextHolder.getLocale()
        );
    }

    /**
     * ErrorCode에서 메시지 가져오기 (파라미터 포함)
     *
     * 예시:
     * messages.properties에 "error.user.not.found=사용자 {0}를 찾을 수 없습니다."
     * getMessage(ErrorCode.USER_NOT_FOUND, "user123") -> "사용자 user123를 찾을 수 없습니다."
     */
    public String getMessage(ErrorCode errorCode, Object... args) {
        return messageSource.getMessage(
                errorCode.getMessageKey(),
                args,
                errorCode.getCode(),
                LocaleContextHolder.getLocale()
        );
    }

    /**
     * 메시지 키로 직접 메시지 가져오기
     */
    public String getMessage(String messageKey) {
        return messageSource.getMessage(
                messageKey,
                null,
                messageKey,
                LocaleContextHolder.getLocale()
        );
    }

    /**
     * 메시지 키로 직접 메시지 가져오기 (파라미터 포함)
     */
    public String getMessage(String messageKey, Object... args) {
        return messageSource.getMessage(
                messageKey,
                args,
                messageKey,
                LocaleContextHolder.getLocale()
        );
    }
}
