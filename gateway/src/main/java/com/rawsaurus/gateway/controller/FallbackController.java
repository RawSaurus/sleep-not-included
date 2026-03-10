package com.rawsaurus.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("/user")
    public Mono<ResponseEntity<Map<String, Object>>> userFallback(ServerWebExchange exchange){
        String method = exchange.getRequest().getMethod().name();
        HttpStatus status = isCircuitOpen(exchange)
                ? HttpStatus.SERVICE_UNAVAILABLE
                : HttpStatus.GATEWAY_TIMEOUT;
        return Mono.just(ResponseEntity.status(status).body(
                Map.of(
                        "service", "user",
                        "status", status.value(),
                        "message", "User service is unavailable. Wait a while and retry"
                ))
        );
    }

    @RequestMapping("/build")
    public Mono<ResponseEntity<Map<String, Object>>> buildFallback(ServerWebExchange exchange){
        String method = exchange.getRequest().getMethod().name();
        HttpStatus status = isCircuitOpen(exchange)
                ? HttpStatus.SERVICE_UNAVAILABLE
                : HttpStatus.GATEWAY_TIMEOUT;
        return Mono.just(ResponseEntity.status(status).body(
                Map.of(
                        "service", "build",
                        "status", status.value(),
                        "message", "Build service is unavailable. Wait a while and retry"
                ))
        );
    }

    @RequestMapping("/comment")
    public Mono<ResponseEntity<Map<String, Object>>> commentFallback(ServerWebExchange exchange){
        String method = exchange.getRequest().getMethod().name();
        HttpStatus status = isCircuitOpen(exchange)
                ? HttpStatus.SERVICE_UNAVAILABLE
                : HttpStatus.GATEWAY_TIMEOUT;
        return Mono.just(ResponseEntity.status(status).body(
                Map.of(
                        "service", "comment",
                        "status", status.value(),
                        "message", "Comment service is unavailable. Wait a while and retry"
                ))
        );
    }

    @RequestMapping("/tag")
    public Mono<ResponseEntity<Map<String, Object>>> tagFallback(ServerWebExchange exchange){
        String method = exchange.getRequest().getMethod().name();
        HttpStatus status = isCircuitOpen(exchange)
                ? HttpStatus.SERVICE_UNAVAILABLE
                : HttpStatus.GATEWAY_TIMEOUT;
        return Mono.just(ResponseEntity.status(status).body(
                Map.of(
                        "service", "tag",
                        "status", status.value(),
                        "message", "Tag service is unavailable. Wait a while and retry"
                ))
        );
    }

    @RequestMapping("/res")
    public Mono<ResponseEntity<Map<String, Object>>> gameresFallback(ServerWebExchange exchange){
        String method = exchange.getRequest().getMethod().name();
        HttpStatus status = isCircuitOpen(exchange)
                ? HttpStatus.SERVICE_UNAVAILABLE
                : HttpStatus.GATEWAY_TIMEOUT;
        return Mono.just(ResponseEntity.status(status).body(
                Map.of(
                        "service", "Resource",
                        "status", status.value(),
                        "message", "Resource service is unavailable. Wait a while and retry"
                ))
        );
    }

    @RequestMapping("/image")
    public Mono<ResponseEntity<Map<String, Object>>> imageFallback(ServerWebExchange exchange){
        String method = exchange.getRequest().getMethod().name();
        HttpStatus status = isCircuitOpen(exchange)
                ? HttpStatus.SERVICE_UNAVAILABLE
                : HttpStatus.GATEWAY_TIMEOUT;
        return Mono.just(ResponseEntity.status(status).body(
                Map.of(
                        "service", "image",
                        "status", status.value(),
                        "message", "Image service is unavailable. Wait a while and retry"
                ))
        );
    }

    private boolean isCircuitOpen(ServerWebExchange exchange) {
        return exchange.getResponse().getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE;
    }
}
