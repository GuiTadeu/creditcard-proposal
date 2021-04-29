package com.orange.credicard.validation;

import org.springframework.util.Base64Utils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Base64Validator implements ConstraintValidator<Base64, String> {

    @Override
    public void initialize(Base64 base64) {}

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            Base64Utils.decode(value.getBytes(UTF_8));
        } catch (Exception exception) {
            return false;
        }

        return true;
    }
}
