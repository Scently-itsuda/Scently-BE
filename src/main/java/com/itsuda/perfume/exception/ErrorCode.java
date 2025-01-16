package com.itsuda.perfume.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    NOT_FOUND_USER("1404", HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다"),
    NOT_FOUND_PERFUME("1404", HttpStatus.NOT_FOUND, "존재하지 않는 향수입니다"),
    NOT_FOUND_PERFUME_DETAIL("1404", HttpStatus.NOT_FOUND, "존재하지 않는 향수 상세정보입니다"),
    NOT_FOUND_PERFUME_VOLUME("1404", HttpStatus.NOT_FOUND, "존재하지 않는 향수 용량입니다"),
    NOT_FOUND_ACCORD("1404", HttpStatus.NOT_FOUND, "존재하지 않는 향입니다"),
    NOT_FOUND_COMMENT("1404", HttpStatus.NOT_FOUND, "존재하지 않는 댓글입니다"),

    // Bad Request Error
    NOT_END_POINT("1400", HttpStatus.BAD_REQUEST, "존재하지 않는 엔드포인트입니다"),

    // Server, File Up/DownLoad Error
    SERVER_ERROR("1500", HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다"),
    FILE_UPLOAD("1500", HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다"),
    FILE_DOWNLOAD("1500", HttpStatus.INTERNAL_SERVER_ERROR, "파일 다운로드에 실패했습니다"),

    // Access Denied Error
    ACCESS_DENIED_ERROR("1401", HttpStatus.UNAUTHORIZED, "접근이 거부된 토큰입니다"),

    // Token Error Set
    TOKEN_INVALID_ERROR("1401", HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다"),
    TOKEN_EXPIRED_ERROR("1401", HttpStatus.UNAUTHORIZED, "만료된 토큰입니다"),
    TOKEN_TYPE_ERROR("1401", HttpStatus.UNAUTHORIZED, "토큰 타입 오류입니다"),
    TOKEN_UNSUPPORTED_ERROR("1401", HttpStatus.UNAUTHORIZED, "지원하지 않는 토큰입니다"),
    TOKEN_UNKNOWN_ERROR("1401", HttpStatus.UNAUTHORIZED, "알 수 없는 토큰 오류입니다");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;
}