package com.itsuda.perfume.annotation;

import jakarta.validation.Constraint;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jakarta.validation.Payload;

import com.itsuda.perfume.validator.NicknameValidator;

@Documented
@Constraint(validatedBy = NicknameValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidNickname {
    String message() default "닉네임 형식이 올바르지 않습니다. (영문 대소문자, 숫자, 특수문자 조합)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
