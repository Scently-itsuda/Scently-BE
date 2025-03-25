package com.itsuda.perfume.annotation;

import com.itsuda.perfume.validator.TagValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TagValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface ValidTag {

    String message() default "INVALID_TAG";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
