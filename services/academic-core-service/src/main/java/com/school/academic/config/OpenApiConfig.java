package com.school.academic.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI academicCoreServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Academic Core Service API")
                        .description("API for managing the academic structure, people, and placements within the institution. Domain Model: v1.0 (FROZEN)")
                        .version("1.0.0"))
                .servers(List.of(new Server().url("/").description("Base API path")));
    }
}

