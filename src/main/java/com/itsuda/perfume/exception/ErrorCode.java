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
    NOT_FOUND_OOTD("1404", HttpStatus.NOT_FOUND, "존재하지 않는 OOTD 게시글입니다."),
    NOT_FOUNT_POST("1404", HttpStatus.NOT_FOUND, "존재하는 자유게시글입니다."),

    // Bad Request Error
    NOT_END_POINT("1400", HttpStatus.BAD_REQUEST, "존재하지 않는 엔드포인트입니다"),
    INVALID_HEADER("1400", HttpStatus.BAD_REQUEST, "유효하지 않은 헤더입니다"),
    INVALID_DATE_FORMAT("1400", HttpStatus.BAD_REQUEST, "날짜는 yyyy-MM-dd 형식이어야 합니다"),
    INVALID_FUTURE_DATE("1400", HttpStatus.BAD_REQUEST, "미래 날짜는 입력할 수 없습니다"),
    INVALID_NICKNAME_FORMAT("1400", HttpStatus.BAD_REQUEST, "닉네임은 한글, 영문, 숫자만 사용할 수 있습니다"),
    INVALID_NICKNAME_LENGTH("1400", HttpStatus.BAD_REQUEST, "닉네임은 2자 이상 16자 이하여야 합니다"),
    INVALID_COMMENT_COMMENT("1400", HttpStatus.BAD_REQUEST, "댓글은 공백이 아닌 1글자 이상이 포함되어야 합니다."),
    NICKNAME_ALREADY_EXISTS("1400", HttpStatus.BAD_REQUEST, "이미 사용 중인 닉네임입니다"),
    NICKNAME_CONTAINS_FORBIDDEN("1400", HttpStatus.BAD_REQUEST, "사용할 수 없는 단어가 포함되어 있습니다"),
    INVALID_POTENTIAL_TYPE("1400", HttpStatus.BAD_REQUEST, "유효하지 않은 부향률 타입입니다"),
    INVALID_NOTE_TYPE("1400", HttpStatus.BAD_REQUEST, "유효하지 않은 노트 타입입니다"),
    INVALID_BRAND_TYPE("1400", HttpStatus.BAD_REQUEST, "유효하지 않은 브랜드 타입입니다"),
    INVALID_COUNTRY_TYPE("1400", HttpStatus.BAD_REQUEST, "유효하지 않은 국가 타입입니다"),
    INVALID_GENDER_TYPE("1400", HttpStatus.BAD_REQUEST, "유효하지 않은 성별 타입입니다"),
    INVALID_OOTD_SORT_TYPE("1400", HttpStatus.BAD_REQUEST, "유효하지 않은 정렬 타입입니다"),

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
    TOKEN_UNKNOWN_ERROR("1401", HttpStatus.UNAUTHORIZED, "알 수 없는 토큰 오류입니다"), 
    TOKEN_MALFORMED_ERROR("1401", HttpStatus.UNAUTHORIZED, "토큰 형식이 올바르지 않습니다");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;
}
