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
    NOT_FOUND_OOTD("1404", HttpStatus.NOT_FOUND, "존재하지 않는 OOTD 게시글입니다"),
    NOT_FOUND_POST("1404", HttpStatus.NOT_FOUND, "존재하는 자유게시글입니다"),
    NOT_FOUND_FILE_PATH("1404", HttpStatus.NOT_FOUND, "존재하지 않는 파일 경로입니다"),
    NOT_FOUND_REVIEW("1404", HttpStatus.NOT_FOUND, "존재하는 리뷰입니다"),
    DELETED_OOTD("1404", HttpStatus.NOT_FOUND, "이미 삭제된 OOTD 게시글입니다"),
    DELETED_POST("1404", HttpStatus.NOT_FOUND, "이미 삭제된 자유게시글입니다"),
    DELETED_COMMENT("1404", HttpStatus.NOT_FOUND, "이미 삭제된 댓글입니다"),

    // Bad Request Error
    NOT_END_POINT("1400", HttpStatus.BAD_REQUEST, "존재하지 않는 엔드포인트입니다"),
    INVALID_HEADER("1400", HttpStatus.BAD_REQUEST, "유효하지 않은 헤더입니다"),
    INVALID_DATE_FORMAT("1400", HttpStatus.BAD_REQUEST, "날짜는 yyyy-MM-dd 형식이어야 합니다"),
    INVALID_FUTURE_DATE("1400", HttpStatus.BAD_REQUEST, "미래 날짜는 입력할 수 없습니다"),
    INVALID_NICKNAME_FORMAT("1400", HttpStatus.BAD_REQUEST, "닉네임은 한글, 영문, 숫자만 사용할 수 있습니다"),
    INVALID_NICKNAME_LENGTH("1400", HttpStatus.BAD_REQUEST, "닉네임은 2자 이상 16자 이하여야 합니다"),
    INVALID_COMMENT_COMMENT("1400", HttpStatus.BAD_REQUEST, "댓글은 공백이 아닌 1자 이상이 포함되어야 합니다"),
    INVALID_TAG("1400", HttpStatus.BAD_REQUEST, "태그는 공백이 아닌 1~15자여야 하며 공백문자를 포함하면 안됩니다"),
    INVALID_IMAGE("1400", HttpStatus.BAD_REQUEST, "이미지는 jpg, jpeq, png만 지원하며 최소 1장에서 최대 5장까지 첨부해야 합니다."),
    INVALID_PERFUME_LIST("1400", HttpStatus.BAD_REQUEST, "향수는 최소 1개에서 최대 3개까지 등록해야 합니다"),
    NICKNAME_ALREADY_EXISTS("1400", HttpStatus.BAD_REQUEST, "이미 사용 중인 닉네임입니다"),
    NICKNAME_CONTAINS_FORBIDDEN("1400", HttpStatus.BAD_REQUEST, "사용할 수 없는 단어가 포함되어 있습니다"),
    INVALID_POTENTIAL_TYPE("1400", HttpStatus.BAD_REQUEST, "유효하지 않은 부향률 타입입니다"),
    INVALID_NOTE_TYPE("1400", HttpStatus.BAD_REQUEST, "유효하지 않은 노트 타입입니다"),
    INVALID_BRAND_TYPE("1400", HttpStatus.BAD_REQUEST, "유효하지 않은 브랜드 타입입니다"),
    INVALID_COUNTRY_TYPE("1400", HttpStatus.BAD_REQUEST, "유효하지 않은 국가 타입입니다"),
    INVALID_GENDER_TYPE("1400", HttpStatus.BAD_REQUEST, "유효하지 않은 성별 타입입니다"),
    INVALID_OOTD_SORT_TYPE("1400", HttpStatus.BAD_REQUEST, "유효하지 않은 정렬 타입입니다"),
    MAX_TAG_SIZE("1400", HttpStatus.BAD_REQUEST, "태그는 최대 10개까지 가능합니다"),
    EMPTY_POST_TITLE("1400", HttpStatus.BAD_REQUEST, "자유게시글의 제목은 공백이 아닌 글자가 있어야합니다"),
    EMPTY_POST_CONTENT("1400", HttpStatus.BAD_REQUEST, "자유게시글의 내용은 공백이 아닌 글자가 있어야합니다"),
    EMPTY_OOTD_CONTENT("1400", HttpStatus.BAD_REQUEST, "OOTD의 내용은 공백이 아닌 글자가 있어야합니다"),
    ONLY_OOTD_OWNER_DELETE("1400", HttpStatus.BAD_REQUEST, "OOTD 작성자만 OOTD 게시글을 삭제할 수 있습니다."),
    ONLY_POST_OWNER_DELETE("1400", HttpStatus.BAD_REQUEST, "자유게시글 작성자만 자유게시글을 삭제할 수 있습니다."),
    ONLY_COMMENT_OWNER_DELETE("1400", HttpStatus.BAD_REQUEST, "댓글 작성자만 댓글을 삭제할 수 있습니다."),
    FCM_TOKEN_INVALID_ERROR("1400", HttpStatus.BAD_REQUEST, "FCM 토큰 형식이 올바르지 않습니다"),
    ALREADY_REPORTED_OOTD("1400", HttpStatus.BAD_REQUEST, "이미 신고한 OOTD입니다"),
    ALREADY_REPORTED_POST("1400", HttpStatus.BAD_REQUEST, "이미 신고한 자유게시글입니다"),
    ALREADY_REPORTED_COMMENT("1400", HttpStatus.BAD_REQUEST, "이미 신고한 댓글입니다"),
    ALREADY_REPORTED_REVIEW("1400", HttpStatus.BAD_REQUEST, "이미 신고한 리뷰입니다"),
    NOT_EXIST_REPORT_TYPE("1400", HttpStatus.BAD_REQUEST, "신고 사유가 있어야 합니다"),

    // Server, File Up/DownLoad Error
    SERVER_ERROR("1500", HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다"),
    FILE_UPLOAD("1500", HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다"),
    FILE_DOWNLOAD("1500", HttpStatus.INTERNAL_SERVER_ERROR, "파일 다운로드에 실패했습니다"),
    FILE_PROCESSING_FAIL("1500", HttpStatus.INTERNAL_SERVER_ERROR, "파일 바이트 처리 중 예외가 발생했습니다"),
    INVALID_FCM_MESSAGE("1500", HttpStatus.INTERNAL_SERVER_ERROR, "푸시 메시지의 제목과 본문이 비어져있습니다"),

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
