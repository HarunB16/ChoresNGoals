package com.choresngoals.config;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.choresngoals.service.DuplicateEmailException;
import com.choresngoals.service.InvalidCredentialsException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateEmail(DuplicateEmailException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiErrorResponse.of(HttpStatus.CONFLICT, exception.getMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidCredentials(InvalidCredentialsException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiErrorResponse.of(HttpStatus.UNAUTHORIZED, exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        List<String> details = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        return ResponseEntity.badRequest()
                .body(ApiErrorResponse.of(HttpStatus.BAD_REQUEST, "Request validation failed", details));
    }

    public record ApiErrorResponse(
            int status,
            String error,
            String message,
            List<String> details,
            Instant timestamp
    ) {
        static ApiErrorResponse of(HttpStatus status, String message) {
            return of(status, message, List.of());
        }

        static ApiErrorResponse of(HttpStatus status, String message, List<String> details) {
            return new ApiErrorResponse(
                    status.value(),
                    status.getReasonPhrase(),
                    message,
                    details,
                    Instant.now()
            );
        }
    }
}
