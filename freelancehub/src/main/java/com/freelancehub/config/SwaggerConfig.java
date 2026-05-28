package com.freelancehub.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FreelanceHub API")
                        .version("v1 - Semana 2 / Fase 1")
                        .description("API REST de FreelanceHub. " +
                                "Para endpoints protegidos: inicia sesión en " +
                                "POST /api/v1/auth/login, copia el campo 'token' " +
                                "de la respuesta y pulsa el botón Authorize (🔒) " +
                                "arriba a la derecha. Pega únicamente el token; " +
                                "Swagger añade automáticamente el prefijo 'Bearer '."))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
