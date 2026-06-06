package com.sandeep.matchingservice.service;


import com.sandeep.matchingservice.event.RideRequestedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RideEventCosumer {

     private final MatchingService matchingService;

    public RideEventCosumer(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    //Listen to ride.requested kafka topic.
    //Triggered every time Ride Service published a new ride request

    //Flow
    // Ride Service -> Kafka (ride.requested) -> This consumer -> MatchingService


    @KafkaListener(topics = "ride.requested",groupId = "matching-service-group-v2")
    public void consumeRideRequestedEvent(RideRequestedEvent event){

        try{
            matchingService.matchDriverForRide(event);
        }
        catch (Exception e){
            log.error("Error processing ride request: {} - {} ",event.getRideId(),e.getMessage());
        }
    }

}
