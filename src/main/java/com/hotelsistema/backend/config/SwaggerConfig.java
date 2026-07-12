package com.hotelsistema.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API Sistema Hotelero",
                description = "Documentación completa de los endpoints del backend del Hotel",
                version = "1.0.0"
        ),
        security = @SecurityRequirement(name = "bearerAuth") // Aplica seguridad a todos los endpoints por defecto
)
@SecurityScheme(
        name = "bearerAuth",
        description = "Pega aquí el Token JWT que te devuelve el endpoint de Login",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class SwaggerConfig {
}