package com.cnbjti.api.common;

import jakarta.validation.ConstraintViolationException;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
    String message = ex.getBindingResult().getFieldErrors().stream()
        .findFirst()
        .map(error -> error.getField() + " " + error.getDefaultMessage())
        .orElse("Validation failed");
    return ResponseEntity.badRequest().body(ApiResponse.error("VALIDATION_ERROR", message));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiResponse<Void>> handleConstraint(ConstraintViolationException ex) {
    return ResponseEntity.badRequest().body(ApiResponse.error("VALIDATION_ERROR", ex.getMessage()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponse<Void>> handleBadRequest(IllegalArgumentException ex) {
    return ResponseEntity.badRequest().body(ApiResponse.error("VALIDATION_ERROR", ex.getMessage()));
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("UNAUTHORIZED", "Invalid username or password"));
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
        .body(ApiResponse.error("METHOD_NOT_ALLOWED", ex.getMessage()));
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ApiResponse<Void>> handleIllegalState(IllegalStateException ex) {
    log.warn("Request failed with illegal state: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(ApiResponse.error("STORAGE_ERROR", ex.getMessage()));
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ApiResponse<Void>> handleNotFound(NoSuchElementException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("NOT_FOUND", ex.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleInternal(Exception ex) {
    log.error("Unhandled request failure", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.error("INTERNAL_ERROR", "Internal server error"));
  }
}
