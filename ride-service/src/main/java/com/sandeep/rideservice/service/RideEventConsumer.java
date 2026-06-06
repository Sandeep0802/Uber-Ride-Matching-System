package com.sandeep.rideservice.service;


import com.sandeep.rideservice.event.RideMatchedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j

public class RideEventConsumer {

    private final RideService rideService;

    public RideEventConsumer(RideService rideService) {
        this.rideService = rideService;
    }


    @KafkaListener(topics = "ride.matched",groupId = "ride-service-group")
    public void consumeRideMatchedEvent(RideMatchedEvent event){
        rideService.updateRideWithDriver(
                event.getRideId(),
                event.getDriverId()
        );

    }
}
