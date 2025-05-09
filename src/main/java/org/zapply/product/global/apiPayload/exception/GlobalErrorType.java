package org.zapply.product.global.apiPayload.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GlobalErrorType implements ErrorType {

    // Member
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "토큰이 존재하지 않습니다."),
    TOKEN_INVALID(HttpStatus.BAD_REQUEST, "토큰이 유효하지 않습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 멤버입니다."),
    MEMBER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 멤버입니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),

    // Account
    ALREADY_EXIST_ACCOUNT(HttpStatus.BAD_REQUEST, "이미 존재하는 계정입니다."),
    EMAIL_NOT_FOUND(HttpStatus.BAD_REQUEST, "이메일이 존재하지 않습니다."),
    FACEBOOK_API_ERROR(HttpStatus.BAD_REQUEST, "페이스북 API 호출에 실패했습니다."),
    THREADS_API_ERROR(HttpStatus.BAD_REQUEST, "스레드 API 호출에 실패했습니다."),
    ACCOUNT_TOKEN_KEY_NOT_FOUND(HttpStatus.NOT_FOUND, "계정의 토큰 키를 찾을 수 없습니다."),
    SNS_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 SNS 타입입니다."),

    //Redis
    REDIS_SET_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Redis에 값을 저장하는 데 실패했습니다."),
    REDIS_GET_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Redis에서 값을 가져오는 데 실패했습니다."),
    REDIS_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Redis에서 값을 삭제하는 데 실패했습니다."),
    SHA256_GENERATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SHA256 해시 생성에 실패했습니다."),

    // Common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 내부 오류입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근이 금지되었습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다."),
    JSON_PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "JSON 처리 중 오류가 발생했습니다."),

    // OAuth
    OAUTH_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "로그인 중 오류가 발생했습니다."),

    // CoolSMS
    SMS_UNAUTHENTICATED(HttpStatus.INTERNAL_SERVER_ERROR, "인증번호가 일치하지 않습니다."),

    // Vault
    VAULT_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "Vault에 토큰이 존재하지 않습니다."),
    ;

    private final HttpStatus status;

    private final String message;
}
