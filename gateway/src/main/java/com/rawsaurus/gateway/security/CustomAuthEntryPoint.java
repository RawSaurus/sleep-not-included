package com.rawsaurus.gateway.security;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class CustomAuthEntryPoint implements ServerAuthenticationEntryPoint {

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        if (ex instanceof InvalidBearerTokenException) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            DataBuffer buffer = response.bufferFactory()
                    .wrap(ex.getMessage().getBytes());
//                    .wrap("{\"error\": \"Invalid or expired token\"}".getBytes(), ex.getMessage().getBytes());
//                   byte[] bytes1 = "{\"error\": \"Invalid or expired token\"}".getBytes();
//                   byte[] bytes2 = ex.getMessage().getBytes();
//            List<Byte> bytes = new ArrayList<>();
            return response.writeWith(Mono.just(buffer));
        }
        return Mono.empty();
    }
}