package com.microservicio.inventario.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI inventarioOpenAPI() {
        return new OpenAPI().info(new Info().title("Microservicio de Inventario")
            .description("Stock, reservas, movimientos y alertas de productos").version("1.0"));
    }
}
