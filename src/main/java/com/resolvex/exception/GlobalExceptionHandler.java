package com.resolvex.exception;

import java.time.Instant;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler
        extends ResponseEntityExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            Object body,
            HttpHeaders headers,
            HttpStatusCode statusCode,
            WebRequest request) {

        String message = getErrorName(statusCode);

        if (ex instanceof ResponseStatusException statusException) {
            if (statusException.getReason() != null) {
                message = statusException.getReason();
            }
        } else if (ex instanceof HttpMessageNotReadableException) {
            message = "Request body is missing or contains invalid JSON or field values";
        } else if (ex instanceof MethodArgumentNotValidException validationException) {
            message = validationException.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(error -> error.getField()
                            + ": " + error.getDefaultMessage())
                    .distinct()
                    .collect(Collectors.joining("; "));

            if (message.isBlank()) {
                message = "Request validation failed";
            }
        }

        if (statusCode.is5xxServerError()) {
            log.error("Request failed at {}", getPath(request), ex);
            message = "An unexpected server error occurred";
        }

        ApiErrorResponse response = createError(
                statusCode, message, request);

        return super.handleExceptionInternal(
                ex, response, headers, statusCode, request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDatabaseConflict(
            DataIntegrityViolationException ex,
            WebRequest request) {

        log.error("Database conflict at {}", getPath(request), ex);

        return buildResponse(
                HttpStatus.CONFLICT,
                "The operation conflicts with existing data or database constraints",
                request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(
            AccessDeniedException ex,
            WebRequest request) {

        return buildResponse(
                HttpStatus.FORBIDDEN,
                "You do not have permission to perform this action",
                request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthentication(
            AuthenticationException ex,
            WebRequest request) {

        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                "Authentication failed",
                request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnexpectedException(
            Exception ex,
            WebRequest request) {

        log.error("Unexpected error at {}", getPath(request), ex);

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected server error occurred",
                request);
    }

    private ResponseEntity<Object> buildResponse(
            HttpStatus status,
            String message,
            WebRequest request) {

        return ResponseEntity.status(status)
                .body(createError(status, message, request));
    }

    private ApiErrorResponse createError(
            HttpStatusCode status,
            String message,
            WebRequest request) {

        return new ApiErrorResponse(
                Instant.now(),
                status.value(),
                getErrorName(status),
                message,
                getPath(request));
    }

    private String getErrorName(HttpStatusCode status) {

        HttpStatus knownStatus = HttpStatus.resolve(status.value());

        return knownStatus != null
                ? knownStatus.getReasonPhrase()
                : "HTTP Error";
    }

    private String getPath(WebRequest request) {

        if (request instanceof ServletWebRequest servletRequest) {
            return servletRequest.getRequest().getRequestURI();
        }

        return "";
    }
}