package com.web;


import org.springframework.data.domain.Pageable;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MaxPageSizeValidator implements ConstraintValidator<MaxPageSize, Pageable>{
	 private int maxSize;

	    @Override
	    public void initialize(MaxPageSize constraintAnnotation) {
	        this.maxSize = constraintAnnotation.value();
	    }

	    @Override
	    public boolean isValid(Pageable pageable, ConstraintValidatorContext context) {
	        return pageable == null || pageable.getPageSize() <= maxSize;
	    }
}
