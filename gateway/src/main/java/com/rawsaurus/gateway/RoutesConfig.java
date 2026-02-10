package com.rawsaurus.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
class RoutesConfig {

//    @Bean
    RouteLocator routeLocator(RouteLocatorBuilder builder){
        return builder.routes()
                .route("sleep-not-included-user",
                        r -> r
                                .path("/user/**")
                                .filters(f -> f.rewritePath(
                                        "/user/(?<segment>.*)",
                                        "/api/v1/user/${segment}"))
                                .uri("lb://SLEEP-NOT-INCLUDED-USER")
                )
                .build();
    }
}
