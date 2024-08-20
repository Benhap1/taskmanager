package com.example.taskmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(com.example.taskmanagement.exception.ResourceNotFoundException.class)
    public ResponseEntity<com.example.taskmanagement.exception.ErrorResponse> handleResourceNotFoundException(com.example.taskmanagement.exception.ResourceNotFoundException ex) {
        com.example.taskmanagement.exception.ErrorResponse errorResponse = new com.example.taskmanagement.exception.ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<com.example.taskmanagement.exception.ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        com.example.taskmanagement.exception.ErrorResponse errorResponse = new com.example.taskmanagement.exception.ErrorResponse(HttpStatus.BAD_REQUEST, errors.toString());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
