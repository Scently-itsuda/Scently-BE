package com.itsuda.perfume.annotation;

import com.itsuda.perfume.validator.ImagesValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ImagesValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface ValidImageFile {

    String message() default "INVALID_IMAGE";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
