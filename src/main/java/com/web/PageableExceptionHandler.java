package com.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class PageableExceptionHandler {
	
	  @ExceptionHandler(ConstraintViolationException.class)
	    public ResponseEntity<Map<String, String>> handleValidationExceptions(
	        ConstraintViolationException ex
	    ) {
	        Map<String, String> errors = new HashMap<>();
	        ex.getConstraintViolations().forEach(violation -> {
	            String field = violation.getPropertyPath().toString();
	            errors.put(field, violation.getMessage());
	        });
	        return ResponseEntity.badRequest().body(errors);
	    }
}

