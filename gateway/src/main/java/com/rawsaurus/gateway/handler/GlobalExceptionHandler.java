package com.rawsaurus.gateway.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.webflux.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

import static com.rawsaurus.gateway.handler.BusinessErrorCodes.*;


@Component
@Order(-1)
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status;
        ExceptionResponse body;

        if (ex instanceof RequestNotPermitted) {
            status = HttpStatus.TOO_MANY_REQUESTS;
            body = ExceptionResponse.builder()
                    .businessErrorCode(RATE_LIMIT_EXCEEDED.getCode())
                    .businessErrorDescription(RATE_LIMIT_EXCEEDED.getDescription())
                    .error(ex.getMessage())
                    .build();

        } else if (ex instanceof CallNotPermittedException) {
            // Circuit breaker is open
            status = HttpStatus.SERVICE_UNAVAILABLE;
            body = ExceptionResponse.builder()
                    .businessErrorCode(CIRCUIT_OPEN.getCode())
                    .businessErrorDescription(CIRCUIT_OPEN.getDescription())
                    .error(ex.getMessage())
                    .build();

        } else if (ex instanceof AuthenticationException) {
            status = HttpStatus.UNAUTHORIZED;
            body = ExceptionResponse.builder()
                    .businessErrorCode(UNAUTHORIZED.getCode())
                    .businessErrorDescription(UNAUTHORIZED.getDescription())
                    .error(ex.getMessage())
                    .build();

        } else if (ex instanceof AccessDeniedException) {
            status = HttpStatus.FORBIDDEN;
            body = ExceptionResponse.builder()
                    .businessErrorCode(ACCESS_DENIED.getCode())
                    .businessErrorDescription(ACCESS_DENIED.getDescription())
                    .error(ex.getMessage())
                    .build();

        } else if (ex instanceof NotFoundException) {
            // No downstream route/service found
            status = HttpStatus.NOT_FOUND;
            body = ExceptionResponse.builder()
                    .businessErrorCode(ROUTE_NOT_FOUND.getCode())
                    .businessErrorDescription(ROUTE_NOT_FOUND.getDescription())
                    .error(ex.getMessage())
                    .build();

        } else if (ex instanceof ResponseStatusException rse) {
            status = HttpStatus.valueOf(rse.getStatusCode().value());
            body = ExceptionResponse.builder()
                    .businessErrorCode(status.value())
                    .businessErrorDescription(status.getReasonPhrase())
                    .error(rse.getReason())
                    .build();

        } else if (ex instanceof BusinessException) {
            status = HttpStatus.BAD_REQUEST;
            body = ExceptionResponse.builder()
                    .businessErrorCode(HttpStatus.BAD_REQUEST.value())
                    .businessErrorDescription(ex.getMessage())
                    .build();

        } else {
            status = HttpStatus.BAD_REQUEST;
            body = ExceptionResponse.builder()
                    .businessErrorCode(HttpStatus.BAD_REQUEST.value())
                    .businessErrorDescription("Something went wrong")
                    .error(ex.getMessage())
                    .build();
        }
//        } else {
//            status = HttpStatus.INTERNAL_SERVER_ERROR;
//            body = ExceptionResponse.builder()
//                    .businessErrorDescription("Internal gateway error, contact the admin")
//                    .error(ex.getMessage())
//                    .build();
//        }

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(body);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (Exception e) {
            return exchange.getResponse().setComplete();
        }
    }

//    @ExceptionHandler(HandlerMethodValidationException.class)
//    public ResponseEntity<ExceptionResponse> handleException(HandlerMethodValidationException exp){
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                .body(
//                        ExceptionResponse.builder()
//                                .businessErrorCode(VALIDATION_FAILURE.getCode())
//                                .businessErrorDescription(VALIDATION_FAILURE.getDescription())
//                                .build()
//                );
//    }
//
//    @ExceptionHandler(ActionNotAllowed.class)
//    public ResponseEntity<ExceptionResponse> handleException(ActionNotAllowed exp){
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                .body(ExceptionResponse.builder()
//                        .businessErrorCode(ACTION_NOT_ALLOWED.getCode())
//                        .businessErrorDescription(ACTION_NOT_ALLOWED.getDescription())
//                        .error(exp.getMessage())
//                        .build()
//                );
//    }
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ExceptionResponse> handleException(MethodArgumentNotValidException exp){
//        Set<String> errors = new HashSet<>();
//        exp.getBindingResult().getAllErrors()
//                .forEach(error -> {
//                    var errorMessage = error.getDefaultMessage();
//                    errors.add(errorMessage);
//                });
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                .body(
//                        ExceptionResponse.builder()
//                                .businessErrorCode(VALIDATION_FAILURE.getCode())
//                                .businessErrorDescription(VALIDATION_FAILURE.getDescription())
//                                .validationErrors(errors)
//                                .build()
//                );
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ExceptionResponse> handleException(Exception exp){
//        exp.printStackTrace();
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(
//                        ExceptionResponse.builder()
//                                .businessErrorDescription("Internal error, contact the admin")
//                                .error(exp.getMessage())
//                                .build()
//                );
//    }
}
