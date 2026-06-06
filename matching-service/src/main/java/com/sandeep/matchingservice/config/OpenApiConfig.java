package com.sandeep.matchingservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Matching Service")
                                .version("1.0")
                                .description("""
                                        Internal event-driven service.

                                        Responsibilities:
                                        • Consumes ride.requested events
                                        • Finds nearby drivers
                                        • Calculates matching scores
                                        • Selects best driver
                                        • Publishes ride.matched events

                                        This service exposes no public REST APIs.
                                        Communication occurs through Kafka and
                                        service-to-service calls.
                                        """)
                );
    }
}
