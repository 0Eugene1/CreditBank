package com.example.calculator.config;

import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springdoc.core.models.GroupedOpenApi;

@Configuration
@EnableWebMvc
public class OpenApiConfig implements WebMvcConfigurer {

    @Bean
    public GroupedOpenApi offersApi() {
        return GroupedOpenApi.builder()
                .group("Offers API")
                .pathsToMatch("/calculator/offers")
                .build();
    }

    @Bean
    public GroupedOpenApi calcApi() {
        return GroupedOpenApi.builder()
                .group("Calculation API")
                .pathsToMatch("/calculator/calc")
                .build();
    }
}