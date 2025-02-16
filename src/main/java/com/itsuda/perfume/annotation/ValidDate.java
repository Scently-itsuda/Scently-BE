package com.itsuda.perfume.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

import com.itsuda.perfume.validator.DateValidator;

@Documented
@Constraint(validatedBy = DateValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDate {
    String message() default "INVALID_DATE_FORMAT";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
} 