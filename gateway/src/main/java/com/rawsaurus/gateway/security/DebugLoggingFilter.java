package com.rawsaurus.gateway.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@Profile("dev")
public class DebugLoggingFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(DebugLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        log.info(">>> Incoming: {} {}", request.getMethod(), request.getPath());
        log.info(">>> Headers: {}", request.getHeaders());
        log.info(">>> Path: {}", request.getPath());
        log.info(">>> QueryParams: {}", request.getQueryParams());
        log.info(">>> Body: {}", request.getBody());
        log.info(">>> URI: {}", request.getURI());
        log.info(">>> Cookies: {}", request.getCookies());
        log.info(">>> Attributes: {}", exchange.getAttributes());

        return chain.filter(exchange).doAfterTerminate(() -> {
            log.info("<<< Response status: {}", exchange.getResponse().getStatusCode());
        });
    }
}