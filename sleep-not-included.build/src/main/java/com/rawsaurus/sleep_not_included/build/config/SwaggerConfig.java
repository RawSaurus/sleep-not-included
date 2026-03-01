package com.rawsaurus.sleep_not_included.build.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI buildServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Build Service API")
                        .version("1.0.0")
                        .description("Build management operations"));
    }
}
