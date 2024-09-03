package com.appshop.back_shop.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfiguration {
    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("API Documentation")
                        .version("2.0.0")
                        .description("This is the API documentation for my Shop application.")
                        .contact(new Contact().name("Nguyen Phung Nam").email("namhien12082003@gmail.com"))
                );
    }
}
