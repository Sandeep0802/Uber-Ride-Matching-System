package com.sandeep.rideservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    //Topic where ride service publish ride request
    // Matching service subscribe to this topic
    @Bean
     public NewTopic rideRequestTopic(){
         return TopicBuilder.name("ride.requested")
                 .partitions(3)
                 .replicas(1)
                 .build();
     }


     //Topic where matching service publishes match result
    //ride service subscribe to this topic
    @Bean
    public NewTopic rideMatchTopic(){
         return TopicBuilder.name("ride.matched")
                 .partitions(3)
                 .replicas(1)
                 .build();
    }
}
