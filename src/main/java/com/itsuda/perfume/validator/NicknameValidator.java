package com.itsuda.perfume.validator;

import com.itsuda.perfume.annotation.ValidNickname;
import com.itsuda.perfume.exception.ErrorCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NicknameValidator implements ConstraintValidator<ValidNickname, String> {

    @Override
    public boolean isValid(String nickname, ConstraintValidatorContext context) {
        ErrorCode errorCode = validateNickname(nickname);
        if (errorCode != null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorCode.name())  // enum 이름을 메시지로 사용
                   .addConstraintViolation();
            return false;
        }
        return true;
    }

    private ErrorCode validateNickname(String nickname) {
        // 길이 체크 (null, 빈 값, 2-16자)
        if (nickname == null || nickname.isBlank() || nickname.length() < 2 || nickname.length() > 16) {
            return ErrorCode.INVALID_NICKNAME_LENGTH;
        }
        
        // 정규식 패턴 검사
        if (!nickname.matches(ValidationPatterns.NICKNAME_PATTERN)) {
            return ErrorCode.INVALID_NICKNAME_FORMAT;
        }
        
        // 금칙어 포함 여부 확인
        for (String forbidden : ValidationConstants.FORBIDDEN_WORDS) {
            if (nickname.contains(forbidden)) {
                return ErrorCode.NICKNAME_CONTAINS_FORBIDDEN;
            }
        }
        
        return null;  // 유효한 경우
    }
}
