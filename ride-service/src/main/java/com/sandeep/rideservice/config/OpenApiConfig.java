package com.sandeep.rideservice.config;

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
                                .title("Ride Service API")
                                .version("1.0")
                                .description("""
                                        Ride lifecycle management service.

                                        Responsibilities:
                                        • Create ride requests
                                        • Publish ride.requested events to Kafka
                                        • Receive ride.matched events
                                        • Manage ride state transitions

                                        Ride State Flow:

                                        REQUESTED
                                            ->
                                        MATCHING
                                            ->
                                        ACCEPTED
                                            ->
                                        STARTED
                                            ->
                                        COMPLETED

                                        Technologies:
                                        • Spring Boot
                                        • Kafka
                                        • MySQL
                                        • REST APIs
                                        """)
                );
    }
}
