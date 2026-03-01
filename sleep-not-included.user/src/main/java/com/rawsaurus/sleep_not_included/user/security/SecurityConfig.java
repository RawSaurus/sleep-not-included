package com.rawsaurus.sleep_not_included.user.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Configuration
@EnableWebSecurity
//@EnableMethodSecurity
public class SecurityConfig {

    @Value("${sni.security.client-id-frontend}")
    private String clientIdFrontend;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http){
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(exchange -> exchange
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/webjars/**",
                                "/swagger-resources/**",
                                "/api-docs/**"
                        ).permitAll()
                        .requestMatchers(
                                "/actuator/health",
                                "/actuator/info",
                                "/actuator/prometheus"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(grantedAuthoritiesExtractor())))
        .build();
    }

//    private Converter<Jwt, AbstractAuthenticationToken> grantedAuthoritiesExtractor() {
//
//        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
//
//        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
//            Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
//
//            if (resourceAccess == null) return List.of();
//
//            Map<String, Object> client =
//                    (Map<String, Object>) resourceAccess.get("oauth2-pkce");
//
//            if (client == null) return List.of();
//
//            List<String> roles = (List<String>) client.get("roles");
//
//            if (roles == null) return List.of();
//
//            return roles.stream()
//                    .map(role -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + role))
//                    .toList();
//        });
//
//        return converter;
//    }

    private Converter<Jwt, AbstractAuthenticationToken> grantedAuthoritiesExtractor(){
        JwtAuthenticationConverter jwtAuthenticationConverter =
                new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt ->{
            List<String> roles = jwt.getClaimAsMap("resource_access")
                    .entrySet().stream()
                    .filter(entry -> entry.getKey().equals(clientIdFrontend))
                    .flatMap(entry -> ((Map<String, List<String>>) entry.getValue())
                            .get("roles").stream())
                    .toList();

            System.out.println("roles " + roles);
            return roles.stream()
                    .map(role -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + role))
                    .toList();
        });
        return jwtAuthenticationConverter;
    }
}
