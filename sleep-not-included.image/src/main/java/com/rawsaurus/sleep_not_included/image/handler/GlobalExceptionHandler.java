package com.rawsaurus.sleep_not_included.image.handler;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessagingException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.HashSet;
import java.util.Set;

import static com.rawsaurus.sleep_not_included.image.handler.BusinessErrorCodes.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionResponse> handleException(DataIntegrityViolationException exp){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(ITEM_ALREADY_EXISTS.getCode())
                                .businessErrorDescription(ITEM_ALREADY_EXISTS.getDescription())
                                .build()
                );
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ExceptionResponse> handleException(HandlerMethodValidationException exp){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(VALIDATION_FAILURE.getCode())
                                .businessErrorDescription(VALIDATION_FAILURE.getDescription())
                                .build()
                );
    }

    @ExceptionHandler(ActionNotAllowed.class)
    public ResponseEntity<ExceptionResponse> handleException(ActionNotAllowed exp){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(ACTION_NOT_ALLOWED.getCode())
                        .businessErrorDescription(ACTION_NOT_ALLOWED.getDescription())
                        .error(exp.getMessage())
                        .build()
                );
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ExceptionResponse> handleException(StorageException exp){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(STORAGE_ERROR.getCode())
                                .error(exp.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(EntityNotFoundException exp){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(ENTITY_NOT_FOUND.getCode())
                                .error(exp.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ExceptionResponse> handleException(MessagingException exp){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.builder()
                                .error(exp.getMessage())
                                .build()
                );
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleException(MethodArgumentNotValidException exp){
        Set<String> errors = new HashSet<>();
        exp.getBindingResult().getAllErrors()
                .forEach(error -> {
                    var errorMessage = error.getDefaultMessage();
                    errors.add(errorMessage);
                });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(VALIDATION_FAILURE.getCode())
                                .businessErrorDescription(VALIDATION_FAILURE.getDescription())
                                .validationErrors(errors)
                                .build()
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception exp){
        exp.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorDescription("Internal error, contact the admin")
                                .error(exp.getMessage())
                                .build()
                );
    }
}
