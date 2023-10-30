package com.example.uchebapi.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                description = "OpenAPI documentation for Ucheba",
                title = "Ucheba API",
                version = "0.0.0.1"
        ),
        servers = {
                @Server(
                        description = "Local ENV",
                        url = "https://localhost"
                ),
                @Server(
                        description = "Prod ENV",
                        url = "https://uchebapi.ru"
                )
        }
)
@SecurityScheme(
        type = SecuritySchemeType.HTTP,
        name = "Bearer Authentication",
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {
}