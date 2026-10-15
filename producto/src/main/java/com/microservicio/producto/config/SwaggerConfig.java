package com.microservicio.producto.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI productoOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Microservicio de Producto")
                        .description(
                                "API para gestionar productos, categorías, "
                                + "imágenes y especificaciones del catálogo")
                        .version("1.0"));
    }
}