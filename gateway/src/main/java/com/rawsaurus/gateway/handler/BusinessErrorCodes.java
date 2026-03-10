package com.rawsaurus.gateway.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum BusinessErrorCodes {
    NO_CODE(0, NOT_IMPLEMENTED, "No code"),
    SERVICE_UNAVAILABLE(503, HttpStatus.SERVICE_UNAVAILABLE, "Service is temporarily unavailable"),
    GATEWAY_TIMEOUT(504, HttpStatus.GATEWAY_TIMEOUT, "Service did not respond in time"),
    RATE_LIMIT_EXCEEDED(429, TOO_MANY_REQUESTS, "Too many requests, slow down"),
    CIRCUIT_OPEN(503, HttpStatus.SERVICE_UNAVAILABLE, "Circuit breaker is open"),
    UNAUTHORIZED(401, HttpStatus.UNAUTHORIZED, "Authentication required"),
    ACCESS_DENIED(403, FORBIDDEN, "You do not have permission for this action"),
    ROUTE_NOT_FOUND(404, NOT_FOUND, "Requested route not found");
    ;

    @Getter
    private final int code;
    @Getter
    private final String description;
    @Getter
    private final HttpStatus httpstatus;


    BusinessErrorCodes(int code, HttpStatus httpstatus, String description) {
        this.code = code;
        this.httpstatus = httpstatus;
        this.description = description;
    }
}
