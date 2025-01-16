package com.itsuda.perfume.exception;

import java.util.Collections;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
@AllArgsConstructor
public class ResponseDto<T> {
    private final String result;    // HTTP 상태 코드 또는 에러 코드
    private final T data;          // 데이터
    private final String error;     // 에러 상세 정보
    private final String message;   // 메시지

    // 성공 응답 생성자
    public ResponseDto(T data) {
        this.result = "1200";
        this.data = data;
        this.error = null;
        this.message = "";
    }

    // 사용자 정의 Exception 에 따른 ResponseDto 리턴
    public static ResponseEntity<Object> toResponseEntity(RestApiException e) {
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(ResponseDto.builder()
                        .result(e.getErrorCode().getCode())        // ErrorCode에 정의된 에러 코드
                        .data(Collections.emptyList())            // 실패 시 빈 데이터를 넘기기로 합의
                        .error(e.getErrorCode().getHttpStatus().toString())
                        .message(e.getErrorCode().getMessage())
                        .build()
                );
    }

    // 그 외 Exception 에 따른 ResponseDto 리턴
    public static ResponseEntity<Object> toResponseEntity(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDto.builder()
                        .result("1500")                          // 서버 에러 코드
                        .message("서버 내부 오류가 발생했습니다")
                        .error(e.getClass().getSimpleName())
                        .data(Collections.emptyList())          // 실패 시 빈 데이터를 넘기기로 합의
                        .build()
                );
    }
}