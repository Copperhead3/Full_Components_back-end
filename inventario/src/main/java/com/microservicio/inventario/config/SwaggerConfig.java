package com.microservicio.inventario.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI inventarioOpenAPI() {
        return new OpenAPI().info(new Info().title("Microservicio de Inventario")
            .description("Stock, reservas, movimientos y alertas de productos").version("1.0"))
            .servers(List.of(new Server().url("http://localhost:8090/inventario").description("API Gateway")));
    }
}
