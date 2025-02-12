package com.itsuda.perfume.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import jakarta.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 사용자 정의 Exception 처리
    @ExceptionHandler(value = RestApiException.class)
    public ResponseEntity<? extends Object> handleRestApiException(RestApiException e) {
        log.error("HandleApiException throw RestApiException : {}", e.getErrorCode().getMessage());
        return ResponseDto.toResponseEntity(e);
    }

    // Validation Exception 처리 (RequestBody 검증)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<? extends Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        ErrorCode errorCode = ErrorCode.valueOf(
            ex.getBindingResult().getAllErrors().get(0).getDefaultMessage()
        );
        log.error("HandleValidationException : {}", errorCode.getMessage());
        return ResponseDto.toResponseEntity(new RestApiException(errorCode));
    }

    // Validation Exception 처리 (RequestParam, PathVariable 검증)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<? extends Object> handleConstraintViolation(ConstraintViolationException ex) {
        ErrorCode errorCode = ErrorCode.valueOf(
            ex.getConstraintViolations().iterator().next().getMessage()
        );
        log.error("HandleValidationException : {}", errorCode.getMessage());
        return ResponseDto.toResponseEntity(new RestApiException(errorCode));
    }

    // 그 외 Exception 처리
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<? extends Object> handleException(Exception e) {
        log.error("HandleException throw Exception : {}", e.getMessage());
        return ResponseDto.toResponseEntity(e);
    }
}
