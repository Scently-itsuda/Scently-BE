package com.itsuda.perfume.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.itsuda.perfume.security.Constants;

@Configuration
public class SwaggerConfig {
    @Value("${springdoc.server.url}")
    private String serverUrl;

    @Bean
    public OpenAPI openAPI() {
        // JWT 보안 스키마 설정
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name(Constants.AUTHORIZATION_HEADER);  // "Authorization" 헤더 사용

        // API 정보 설정
        Info info = new Info()
                .title("Scently API")
                .description("Scently API 명세서")
                .version("v1.0.0");

        Server server = new Server()
            .url(serverUrl)
            .description("Scently API Server");

        return new OpenAPI()
                .info(info)
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", securityScheme))
                .addServersItem(server);
    }
}