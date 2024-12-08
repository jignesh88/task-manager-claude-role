package com.grabduck.taskmanager.exception;

import com.grabduck.taskmanager.dto.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleTaskNotFoundException(
            TaskNotFoundException ex,
            HttpServletRequest request
    ) {
        return new ResponseEntity<>(
                createErrorResponse(
                        ex.getMessage(),
                        HttpStatus.NOT_FOUND,
                        request.getRequestURI(),
                        null
                ),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(InvalidTaskException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidTaskException(
            InvalidTaskException ex,
            HttpServletRequest request
    ) {
        return new ResponseEntity<>(
                createErrorResponse(
                        ex.getMessage(),
                        HttpStatus.BAD_REQUEST,
                        request.getRequestURI(),
                        null
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        return new ResponseEntity<>(
                createErrorResponse(
                        "Validation failed",
                        HttpStatus.BAD_REQUEST,
                        request.getRequestURI(),
                        details
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleMessageNotReadableException(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        return new ResponseEntity<>(
                createErrorResponse(
                        "Invalid request body",
                        HttpStatus.BAD_REQUEST,
                        request.getRequestURI(),
                        null
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleTypeMismatchException(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request
    ) {
        return new ResponseEntity<>(
                createErrorResponse(
                        "Invalid parameter type for: " + ex.getName(),
                        HttpStatus.BAD_REQUEST,
                        request.getRequestURI(),
                        null
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(UserRegistrationException.class)
    public ResponseEntity<ErrorResponseDto> handleUserRegistrationException(
            UserRegistrationException ex,
            HttpServletRequest request
    ) {
        return new ResponseEntity<>(
                createErrorResponse(
                        ex.getMessage(),
                        HttpStatus.BAD_REQUEST,
                        request.getRequestURI(),
                        null
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(
            Exception ex,
            HttpServletRequest request
    ) {
        return new ResponseEntity<>(
                createErrorResponse(
                        "Internal server error",
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        request.getRequestURI(),
                        null
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    private ErrorResponseDto createErrorResponse(
            String message,
            HttpStatus status,
            String path,
            List<String> details
    ) {
        return ErrorResponseDto.builder()
                .message(message)
                .status(status.value())
                .error(status.getReasonPhrase())
                .path(path)
                .timestamp(LocalDateTime.now())
                .details(details)
                .build();
    }
}
