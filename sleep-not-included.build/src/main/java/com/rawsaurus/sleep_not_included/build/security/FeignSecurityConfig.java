package com.rawsaurus.sleep_not_included.build.security;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Configuration
public class FeignSecurityConfig {

    @Value("${sni.security.internal-api-key}")
    private String internalApiKey;

    @Bean
    public RequestInterceptor requestInterceptor(){
        return requestTemplate -> {
            var auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth instanceof JwtAuthenticationToken jwtAuth) {
                String tokenValue = jwtAuth.getToken().getTokenValue();
                requestTemplate.header("Authorization", "Bearer " + tokenValue);
            }else {
                requestTemplate.header("X-Internal-Api-Key", internalApiKey);
            }
        };
    }
}
