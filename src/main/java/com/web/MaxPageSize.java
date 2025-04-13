package com.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
@Constraint(validatedBy=MaxPageSizeValidator.class)
public @interface MaxPageSize{
	String message() default "Page size cannot exceed {value}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    int value();
}
