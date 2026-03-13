package com.rawsaurus.gateway.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class RateLimiterFilter implements GlobalFilter, Ordered {

    private final Map<String, RateLimiter> rateLimiters;

    public RateLimiterFilter(RateLimiterRegistry rateLimiterRegistry) {
        this.rateLimiters = Map.of(
                "/user",    rateLimiterRegistry.rateLimiter("userRL"),
                "/build",   rateLimiterRegistry.rateLimiter("buildRL"),
                "/comment", rateLimiterRegistry.rateLimiter("commentRL"),
                "/tag",     rateLimiterRegistry.rateLimiter("tagRL"),
                "/gameres", rateLimiterRegistry.rateLimiter("gameresRL"),
                "/image",   rateLimiterRegistry.rateLimiter("imageRL")
        );
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        RateLimiter limiter = rateLimiters.entrySet().stream()
                .filter(e -> path.startsWith(e.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);

        if (limiter == null) return chain.filter(exchange);

        return Mono.defer(() -> chain.filter(exchange))
                .transformDeferred(RateLimiterOperator.of(limiter))
                .onErrorResume(RequestNotPermitted.class, ex -> {
                    exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                    exchange.getResponse().getHeaders().add("Retry-After", "1");
                    return exchange.getResponse().setComplete();
                });
    }

    @Override
    public int getOrder() {
        return -2; // run before routing filters
    }
}
