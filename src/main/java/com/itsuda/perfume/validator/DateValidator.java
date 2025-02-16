package com.itsuda.perfume.validator;

import com.itsuda.perfume.annotation.ValidDate;
import com.itsuda.perfume.exception.ErrorCode;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;

@Slf4j
public class DateValidator implements ConstraintValidator<ValidDate, String> {
    
    @Override
    public boolean isValid(String date, ConstraintValidatorContext context) {
        if (date == null) {
            return true;  // null 허용
        }

        // 1. 날짜 형식 검증
        if (!date.matches(ValidationPatterns.DATE_PATTERN)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorCode.INVALID_DATE_FORMAT.name())
                   .addConstraintViolation();
            return false;
        }

        try {
            // 2. 날짜 파싱
            LocalDate inputDate = LocalDate.parse(date);
            LocalDate now = LocalDate.now(ZoneId.of("Asia/Seoul"));

            // 3. 미래 날짜 검증
            if (inputDate.isAfter(now)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(ErrorCode.INVALID_FUTURE_DATE.name())
                       .addConstraintViolation();
                return false;
            }

            return true;

        } catch (DateTimeParseException e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorCode.INVALID_DATE_FORMAT.name())
                   .addConstraintViolation();
            return false;
        }
    }
} 