package com.itsuda.perfume.validator;

import com.itsuda.perfume.annotation.ValidTag;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class TagValidator implements ConstraintValidator<ValidTag, List<String>> {
    @Override
    public boolean isValid(List<String> strings, ConstraintValidatorContext constraintValidatorContext) {
        return strings != null && strings.stream()
                .allMatch(tag -> !tag.isBlank() && tag.length() <= 15 && !tag.contains(" "));
    }
}
