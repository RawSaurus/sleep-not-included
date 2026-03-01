package com.rawsaurus.gateway.config;

import org.springdoc.core.properties.AbstractSwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Automatically discovers microservices and adds them to Swagger UI
 */
//@Configuration
//public class SwaggerRouteConfig {
//
//    /**
//     * Manually configure swagger URLs for each microservice
//     */
//    @Bean
//    public CommandLineRunner swaggerUrlsConfigurer(
//            SwaggerUiConfigProperties swaggerUiConfigProperties,
//            RouteDefinitionLocator routeDefinitionLocator) {
//
//        return args -> {
//            Set<AbstractSwaggerUiConfigProperties.SwaggerUrl> urls = new HashSet<>();
//
//            // Add each microservice manually
//            urls.add(new AbstractSwaggerUiConfigProperties.SwaggerUrl(
//                    "User Service",
//                    "/api/users/v3/api-docs",
//                    "user-service"
//            ));
//
//            urls.add(new AbstractSwaggerUiConfigProperties.SwaggerUrl(
//                    "Build Service",
//                    "/api/builds/v3/api-docs",
//                    "build-service"
//            ));
//
//            urls.add(new AbstractSwaggerUiConfigProperties.SwaggerUrl(
//                    "Comment Service",
//                    "/api/comments/v3/api-docs",
//                    "comment-service"
//            ));
//
//            swaggerUiConfigProperties.setUrls(urls);
//        };
//    }
//}