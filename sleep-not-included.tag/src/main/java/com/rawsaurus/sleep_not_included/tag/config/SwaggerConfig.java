package com.rawsaurus.sleep_not_included.tag.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI tagServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Tag Service API")
                        .version("1.0.0")
                        .description("Tag management operations"));
    }
}
