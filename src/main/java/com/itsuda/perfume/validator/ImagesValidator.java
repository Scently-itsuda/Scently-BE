package com.itsuda.perfume.validator;

import com.itsuda.perfume.annotation.ValidImageFile;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class ImagesValidator implements ConstraintValidator<ValidImageFile, List<MultipartFile>> {
    @Override
    public boolean isValid(List<MultipartFile> file, ConstraintValidatorContext constraintValidatorContext) {
        return file != null && !file.isEmpty() && file.size() <= 5 &&
                file.stream().allMatch(image -> StringUtils.equalsAny(image.getOriginalFilename().substring(
                                image.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase(),
                        "jpg", "jpeq", "png"));
    }
}
