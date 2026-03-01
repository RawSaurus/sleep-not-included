package com.rawsaurus.gateway.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${sni.security.client-id-frontend}")
    private String clientIdFrontend;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http){
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeExchange(exchange -> exchange
                                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                                .pathMatchers(
                                        "/swagger-ui.html",
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/webjars/**",
                                        "/swagger-resources/**",
                                        "/api-docs/**",
                                        "/api/*/v3/api-docs",      // Service-specific docs
                                        "/api/*/swagger-ui/**"      // Service-specific UI
                                ).permitAll()

                                .pathMatchers(
                                        "/actuator/health",
                                        "/actuator/info",
                                        "/actuator/prometheus"
                                ).permitAll()
                        .anyExchange().permitAll()
                )
                .oauth2ResourceServer(oAuth2 -> oAuth2.jwt(
                        jwt -> jwt.jwtAuthenticationConverter(grantedAuthoritiesExtractor())
                ))
        .build();
    }

    @Bean
    @Order(0)
    public SecurityWebFilterChain publicSecurityWebFilterChain(ServerHttpSecurity http) {
        return http
                .securityMatcher(ServerWebExchangeMatchers.pathMatchers(
                                        "/swagger-ui.html",
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/webjars/**",
                                        "/swagger-resources/**",
                                        "/api-docs/**",
                                        "/api/*/v3/api-docs",
                                        "/api/*/swagger-ui/**",
                                        "/actuator/health",
                                        "/actuator/info",
                                        "/actuator/prometheus"
                        )
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeExchange(exchange -> exchange.anyExchange().permitAll())
//                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.authenticationManager()))
                .build();
    }

//    @Bean
//    @Order(1)
//    public SecurityWebFilterChain apiSecurityWebFilterChain(ServerHttpSecurity http) {
//        return http
//                .csrf(ServerHttpSecurity.CsrfSpec::disable)
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//                .authorizeExchange(exchange -> exchange
//                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
//                        .anyExchange().authenticated()
//                )
//                .oauth2ResourceServer(oAuth2 -> oAuth2.jwt(
//                        jwt -> jwt.jwtAuthenticationConverter(grantedAuthoritiesExtractor())
//                ))
//                .build();
//    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200", "http://localhost:8080"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "DELETE","PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor(){
        ReactiveJwtAuthenticationConverter jwtAuthenticationConverter =
                new ReactiveJwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt ->{
            List<String> roles = jwt.getClaimAsMap("resource_access")
                    .entrySet().stream()
                    .filter(entry -> {
                        System.out.println("entry " + entry.getKey());
                        return entry.getKey().equals(clientIdFrontend);
                    })
                    .flatMap(entry -> {
                        System.out.println("entry value " + entry.getValue());
                        return ((Map<String, List<String>>) entry.getValue())
                            .get("roles").stream();
                    })
                    .toList();

            System.out.println("roles " + roles);

            return Flux.fromIterable(roles)
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role));
        });
        return jwtAuthenticationConverter;
    }
}
