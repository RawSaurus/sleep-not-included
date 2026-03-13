package com.rawsaurus.gateway.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
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
    @Value("${sni.security.allowed-origins}")
    private List<String> allowedOrigins;

    private final CustomAuthEntryPoint customAuthEntryPoint;

    public SecurityConfig(CustomAuthEntryPoint customAuthEntryPoint) {
        this.customAuthEntryPoint = customAuthEntryPoint;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http){
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> {
                    cors.configurationSource(corsConfigurationSource());
                })
                .authorizeExchange(exchange -> exchange
                                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .pathMatchers(
                                        "/swagger-ui.html",
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/webjars/**",
                                        "/swagger-resources/**",
                                        "/api-docs/**",
                                        "/api/*/v3/api-docs",
                                        "/api/*/swagger-ui/**"
                                ).permitAll()
                                .pathMatchers(
                                        "/actuator/health",
                                        "/actuator/info",
                                        "/actuator/prometheus"
                                ).permitAll()
                        .pathMatchers(
                                HttpMethod.GET,
                                "/user/**",
                                "/build/**",
                                "/comment/**",
                                "/tag/**",
                                "/image/**",
                                "/res/**"
                        ).permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oAuth2 -> oAuth2.jwt(
                        jwt ->
                            jwt.jwtAuthenticationConverter(grantedAuthoritiesExtractor()))
                                .authenticationEntryPoint(customAuthEntryPoint)
                        )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthEntryPoint)
                        .accessDeniedHandler((exchange, denied) -> {
                            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                            return exchange.getResponse().setComplete();
                        })
                )
                .anonymous(Customizer.withDefaults())
        .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
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
                        return entry.getKey().equals(clientIdFrontend);
                    })
                    .flatMap(entry -> {
                        return ((Map<String, List<String>>) entry.getValue())
                            .get("roles").stream();
                    })
                    .toList();

            return Flux.fromIterable(roles)
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role));
        });
        return jwtAuthenticationConverter;
    }
}
