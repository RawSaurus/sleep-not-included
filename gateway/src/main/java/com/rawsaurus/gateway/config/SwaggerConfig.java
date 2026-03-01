package com.rawsaurus.gateway.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.*;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${sni.security.keycloak-server-url}")
    private String keycloakUrl;

    @Value("${sni.security.realm}")
    private String realm;

    @Bean
    public OpenAPI customOpenAPI() {

        String authUrl = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/auth";
        String tokenUrl = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("keycloak",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.OAUTH2)
                                        .flows(new OAuthFlows()
                                                .authorizationCode(new OAuthFlow()
                                                        .authorizationUrl(authUrl)
                                                        .tokenUrl(tokenUrl)
                                                        .scopes(new Scopes()
                                                                .addString("openid", "OpenID scope")
                                                        )
                                                )
                                        )
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("keycloak"));
    }

//    @Bean
//    public OpenAPI customOpenAPI() {
//        return new OpenAPI()
//                .info(new Info()
//                        .title("Microservices API Documentation")
//                        .version("1.0.0")
//                        .description("Aggregated API documentation for all microservices")
//                        .contact(new Contact()
//                                .name("RawSaurus")
//                                .email("rawsaurus@gmail.com")
//                                .url(""))
//                        .license(new License()
//                                .name("Apache 2.0")
//                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
//                .servers(List.of(
//                        new Server()
//                                .url("http://localhost:8080")
//                                .description("Gateway Server - Development")
//                        new Server()
//                                .url("https://api.yourdomain.com")
//                                .description("Gateway Server - Production")
//                ));
//    }
//    @Bean
//    public GroupedOpenApi allApis() {
//        return GroupedOpenApi.builder()
//                .group("all-services")
//                .pathsToMatch("/**")
//                .build();
//    }
    @Bean
    public GroupedOpenApi userServiceApi() {
        return GroupedOpenApi.builder()
                .group("user-service")
                .pathsToMatch("/api/v1/v3/**")
                .build();
    }
//
//    /**
//     * Build Service API Documentation
//     */
//    @Bean
//    public GroupedOpenApi buildServiceApi() {
//        return GroupedOpenApi.builder()
//                .group("build-service")
//                .pathsToMatch("/api/builds/**")
//                .build();
//    }
//
//    /**
//     * Comment Service API Documentation
//     */
//    @Bean
//    public GroupedOpenApi commentServiceApi() {
//        return GroupedOpenApi.builder()
//                .group("comment-service")
//                .pathsToMatch("/api/comments/**")
//                .build();
//    }
//    @Bean GroupedOpenApi gatewayApi() {
//        return GroupedOpenApi.builder()
//                .group("gateway")
//                .pathsToMatch("/**")
//                .build();
//    }
//
//    /**
//     * All Services Combined
//     */
//    @Bean
//    public GroupedOpenApi allServicesApi() {
//        return GroupedOpenApi.builder()
//                .group("all-services")
//                .pathsToMatch("/api/v1/**")
//                .build();
//    }
}