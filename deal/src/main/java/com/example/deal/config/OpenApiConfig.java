package com.example.deal.config;

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
    public GroupedOpenApi calculateApi() {
        return GroupedOpenApi.builder()
                .group("Calculate API")
                .pathsToMatch("/deal/statement")
                .build();
    }
    @Bean
    public GroupedOpenApi offerApi() {
        return GroupedOpenApi.builder()
                .group("Offer API")
                .pathsToMatch("/deal/offer/select")
                .build();
    }
        @Bean
        public GroupedOpenApi finishApi() {
            return GroupedOpenApi.builder()
                    .group("FinishReg API")
                    .pathsToMatch("/deal/calculate/**")
                    .build();
        }
    }