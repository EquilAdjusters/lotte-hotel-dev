package com.example.backendlotte.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.example.backendlotte.storage.FileStorageException;
import com.example.backendlotte.global.response.ErrorResponse;
import com.example.backendlotte.global.response.ValidationErrorResponse;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException exception
    ) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse.of(
                    "INVALID_REQUEST",
                    exception.getMessage()
                )
            );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException exception
    ) {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(
                ErrorResponse.of(
                    "ACCESS_DENIED",
                    "해당 기능을 사용할 권한이 없습니다."
                )
            );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception exception
    ) {
        log.error("처리되지 않은 서버 오류", exception);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ErrorResponse.of(
                                "INTERNAL_SERVER_ERROR",
                                "서버 처리 중 오류가 발생했습니다."));
    }
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadableException(
            HttpMessageNotReadableException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ErrorResponse.of(
                                "INVALID_JSON",
                                "요청값의 형식이 올바르지 않습니다."));
    }
    
    
    @ExceptionHandler(IllegalStateException.class)
        public ResponseEntity<ErrorResponse> handleIllegalStateException(
                IllegalStateException exception
        ) {
                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(
                                                ErrorResponse.of(
                                                                "INVALID_ACCOUNT_STATE",
                                                                exception.getMessage()));
        }
        
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ValidationErrorResponse>
                handleMethodArgumentNotValidException(
                MethodArgumentNotValidException exception
        ) {

                Map<String, String> errors = new LinkedHashMap<>();

                exception.getBindingResult()
                                .getFieldErrors()
                                .forEach(fieldError -> errors.putIfAbsent(
                                                fieldError.getField(),
                                                fieldError.getDefaultMessage()));

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(
                                                ValidationErrorResponse.of(errors));
        }
        
        @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
        public ResponseEntity<ErrorResponse>
                handleMethodNotSupportedException(
                HttpRequestMethodNotSupportedException exception
                ) {

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(
                ErrorResponse.of(
                        "METHOD_NOT_ALLOWED",
                        "허용되지 않은 요청 방식입니다."
                )
                );
        }
        
        @ExceptionHandler(NoResourceFoundException.class)
        public ResponseEntity<ErrorResponse>
                handleNoResourceFoundException(
                NoResourceFoundException exception
        ) {

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(
                                                ErrorResponse.of(
                                                                "RESOURCE_NOT_FOUND",
                                                                "요청한 API를 찾을 수 없습니다."));
        }
        
        @ExceptionHandler(FileStorageException.class)
        public ResponseEntity<ErrorResponse>
                handleFileStorageException(
                FileStorageException exception
                ) {

        log.error(
                "파일 저장소 처리 오류",
                exception
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                ErrorResponse.of(
                        "FILE_STORAGE_ERROR",
                        "파일 처리 중 오류가 발생했습니다."
                )
                );
        }
}