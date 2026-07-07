package com.taskmanagementsystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import tools.jackson.databind.exc.InvalidFormatException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<?> handleTaskNotFoundException(TaskNotFoundException ex,
                                                         WebRequest request) {

        return new ResponseEntity<>(
                createErrorBody(HttpStatus.NOT_FOUND, ex.getMessage(), request),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundException(UserNotFoundException ex,
                                                         WebRequest request) {

        return new ResponseEntity<>(
                createErrorBody(HttpStatus.NOT_FOUND, ex.getMessage(), request),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<?> handleUserAlreadyExistException(UserAlreadyExistException ex,
                                                             WebRequest request) {

        return new ResponseEntity<>(
                createErrorBody(HttpStatus.CONFLICT, ex.getMessage(), request),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UnauthorizedTaskAccessException.class)
    public ResponseEntity<?> handleUnauthorizedTaskAccessException(
            UnauthorizedTaskAccessException ex,
            WebRequest request) {

        return new ResponseEntity<>(
                createErrorBody(HttpStatus.FORBIDDEN, ex.getMessage(), request),
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        Map<String, String> validationErrors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            validationErrors.put(error.getField(), error.getDefaultMessage());
        }

        Map<String, Object> body = createErrorBody(
                HttpStatus.BAD_REQUEST,
                "Validation Failed",
                request);

        body.put("validationErrors", validationErrors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleEnumValidation(HttpMessageNotReadableException ex,
                                                  WebRequest request) {

        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException invalidFormatException
                && invalidFormatException.getTargetType().isEnum()) {

            String fieldName =
                    invalidFormatException.getPath().get(0).getPropertyName();

            String allowedValues = Arrays.toString(
                    invalidFormatException.getTargetType().getEnumConstants());

            String message = String.format(
                    "Invalid value for '%s'. Allowed values are %s.",
                    fieldName,
                    allowedValues);

            return new ResponseEntity<>(
                    createErrorBody(HttpStatus.BAD_REQUEST, message, request),
                    HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(
                createErrorBody(HttpStatus.BAD_REQUEST,
                        "Invalid request body.",
                        request),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(
            IllegalArgumentException ex,
            WebRequest request) {

        return new ResponseEntity<>(
                createErrorBody(HttpStatus.BAD_REQUEST,
                        ex.getMessage(),
                        request),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> handleNullPointerException(
            NullPointerException ex,
            WebRequest request) {

        return new ResponseEntity<>(
                createErrorBody(HttpStatus.INTERNAL_SERVER_ERROR,
                        "A null pointer exception occurred: " + ex.getMessage(),
                        request),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleUsernameNotFoundException(
            org.springframework.security.core.userdetails.UsernameNotFoundException ex,
            WebRequest request) {

        return new ResponseEntity<>(
                createErrorBody(HttpStatus.NOT_FOUND,
                        ex.getMessage(),
                        request),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(
            Exception ex,
            WebRequest request) {

        return new ResponseEntity<>(
                createErrorBody(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        ex.getMessage() != null
                                ? ex.getMessage()
                                : "An unexpected error occurred.",
                        request),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Map<String, Object> createErrorBody(HttpStatus status,
                                                String message,
                                                WebRequest request) {

        Map<String, Object> body = new HashMap<>();

        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return body;
    }
}