package com.rawsaurus.sleep_not_included.gameres.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI gameresServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Game Resource Service API")
                        .version("1.0.0")
                        .description("Game Resource management operations"));
    }
}
