package com.orange.credicard.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = Base64Validator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Base64 {

    String message() default "Invalid base64!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
