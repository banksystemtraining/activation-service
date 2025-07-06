package com.itgirl.web.exception;

import com.itgirl.web.dto.ApiResponse;
import feign.FeignException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.lettuce.core.RedisCommandTimeoutException;
import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.time.Instant;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({ValidationException.class, MethodArgumentNotValidException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiResponse> handleBadRequestException(Exception e) {
        String message;
        if (e instanceof MethodArgumentNotValidException exception) {
            message = exception.getBindingResult().getFieldErrors().stream()
                    .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                    .collect(Collectors.joining("; "));
        } else {
            message = e.getMessage();
        }
        log.warn("Bad request: {}", message);
        return ResponseEntity.badRequest().body(new ApiResponse(message, HttpStatus.BAD_REQUEST.value(), Instant.now()));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiResponse> handleFeignException(FeignException e) {
        log.error("UserCore service error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ApiResponse(
                "User service unavailable. Please try again later", HttpStatus.SERVICE_UNAVAILABLE.value(), Instant.now()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGlobalException(Exception e) {
        log.error("Unexpected error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(
                "Internal server error. Please contact support", HttpStatus.INTERNAL_SERVER_ERROR.value(), Instant.now()));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse> handleJwtException(JwtException e) {
        if (e instanceof ExpiredJwtException) {
            log.error("JWT expired: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(
                    "Token has expired", HttpStatus.UNAUTHORIZED.value(), Instant.now()));
        }
        log.error("Invalid JWT: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(
                "Invalid token", HttpStatus.UNAUTHORIZED.value(), Instant.now()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse> handleAuthenticationException(AuthenticationException e) {
        log.error("Authentication failed: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(
                "Authentication failed", HttpStatus.UNAUTHORIZED.value(), Instant.now()));
    }

    @ExceptionHandler({AccessDeniedException.class, DisabledException.class, BadCredentialsException.class})
    public ResponseEntity<ApiResponse> handleSecurityExceptions(RuntimeException e) {
        HttpStatus status = e instanceof AccessDeniedException
                ? HttpStatus.FORBIDDEN
                : HttpStatus.UNAUTHORIZED;
        log.warn("Security exception: {}", e.getMessage());
        return ResponseEntity.status(status).body(new ApiResponse(e.getMessage(), status.value(), Instant.now()));
    }

    @ExceptionHandler({ServletException.class, IOException.class})
    public ResponseEntity<ApiResponse> handleServletAndIOException(Exception e) {
        log.error("Servlet/IO error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(
                "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value(), Instant.now()));
    }

    @ExceptionHandler({RedisConnectionFailureException.class, RedisSystemException.class, RedisCommandTimeoutException.class})
    public ResponseEntity<ApiResponse> handleRedisExceptions(RuntimeException e) {
        log.error("Redis error: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ApiResponse(
                "Redis service is unavailable. Please try again later.", HttpStatus.SERVICE_UNAVAILABLE.value(), Instant.now()));
    }
}