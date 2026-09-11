package com.poc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI fileStorageOpenApi() {

        return new OpenAPI()
                .info(new Info()
                        .title("File Storage API")
                        .version("1.0")
                        .description(
                                "Spring Boot API for uploading, downloading, "
                                + "listing and deleting files in Amazon S3"));
    }
}