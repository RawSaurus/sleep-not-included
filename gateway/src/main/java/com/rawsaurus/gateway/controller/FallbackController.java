package com.rawsaurus.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/user")
    public Mono<ResponseEntity<String>> userFallback(){
        return Mono.just(
                ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("User service unavailable")
        );
    }

    @GetMapping("/comment")
    public Mono<ResponseEntity<String>> commentFallback(){
        return Mono.just(
                ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("Comment service unavailable")
        );
    }

    @GetMapping("/tag")
    public Mono<ResponseEntity<String>> tagFallback(){
        return Mono.just(
                ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("Tag service unavailable")
        );
    }

    @GetMapping("/build")
    public Mono<ResponseEntity<String>> buildFallback(){
        return Mono.just(
                ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("Build service unavailable")
        );
    }

    @GetMapping("/gameres")
    public Mono<ResponseEntity<String>> gameresFallback(){
        return Mono.just(
                ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("Resource service unavailable")
        );
    }
}
