package com.healthfix.validation;

import com.healthfix.annotation.ValidAvatar;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

public class AvatarValidator implements ConstraintValidator<ValidAvatar, MultipartFile> {

    @Value("${avatar.max-size}")
    private long maxSize;

    @Value("${avatar.allowed-formats}")
    private String allowedFormats;

    private List<String> allowedContentTypes;

    @Override
    public void initialize(ValidAvatar constraintAnnotation) {
        allowedContentTypes = Arrays.asList(allowedFormats.split(","));
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("File cannot be empty.").addConstraintViolation();
            return false;
        }

        String contentType = file.getContentType();
        if (!allowedContentTypes.contains(contentType)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            "Invalid file format. Allowed formats are: " + String.join(", ", allowedContentTypes) + ".")
                    .addConstraintViolation();
            return false;
        }

        if (file.getSize() > maxSize) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("File size exceeds the maximum allowed limit of 5MB.").addConstraintViolation();
            return false;
        }

        return true;
    }
}