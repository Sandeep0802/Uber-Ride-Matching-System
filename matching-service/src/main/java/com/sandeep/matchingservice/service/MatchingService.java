package com.sandeep.matchingservice.service;


import com.sandeep.matchingservice.client.LocationServiceClient;
import com.sandeep.matchingservice.dto.NearByDriverResponse;
import com.sandeep.matchingservice.event.RideMatchedEvent;
import com.sandeep.matchingservice.event.RideRequestedEvent;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MatchingService {


    private final LocationServiceClient locationServiceClient;
  private final KafkaTemplate<String, RideMatchedEvent> kafkaTemplate;

    public MatchingService(LocationServiceClient locationServiceClient, KafkaTemplate<String, RideMatchedEvent> kafkaTemplate) {
        this.locationServiceClient = locationServiceClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    private static final String RIDE_MATCHED_TOPIC="ride.matched";
    private static final double DEFAULT_SEARCH_RADIUS=5.0;



    //Main matching algorithm
    //called when RideRequestedEvent is consumed from kafka

    //Step 1: Ask location service for neaerby drivers

    public void matchDriverForRide(RideRequestedEvent event){

        List<NearByDriverResponse> nearByDrivers=locationServiceClient.getNearByDrivers(
                event.getPickupLongitude(),
                event.getPickupLatitude(),
                DEFAULT_SEARCH_RADIUS
        );

        if(nearByDrivers.isEmpty()){
            log.warn("No drivers found near ride ");
            return;
        }

        //Step 2: Score each driver and pick the best one

        Optional<NearByDriverResponse> bestDriver=findBestDriver(nearByDrivers);

        if(bestDriver.isEmpty()){
            log.warn("could not find suitable driver fro ride");
            return;
        }

        NearByDriverResponse assignedDriver=bestDriver.get();

        //Step 3: Publish ride matched event to kafka

        RideMatchedEvent matchedEvent = new RideMatchedEvent(
                event.getRideId(),
                event.getRiderId(),
                assignedDriver.getDriverId(),
                assignedDriver.getLatitude(),
                assignedDriver.getLongitude(),
                assignedDriver.getDistanceInKm()
        );

        kafkaTemplate.send(RIDE_MATCHED_TOPIC,event.getRideId(),matchedEvent);
        log.info(
                "RideMatchedEvent published for rideId={} driverId={}",
                event.getRideId(),
                assignedDriver.getDriverId()
        );
    }


     //Driver Searching Algo
    //Distance : 70%
    //Rating: 30%

    //score = (1/distance)*distanceWeight + rating*ratingWeight
    private Optional<NearByDriverResponse> findBestDriver(
            List<NearByDriverResponse> drivers) {

        return drivers.stream()
                .max(Comparator.comparingDouble(driver -> {

                    double distanceScore =
                            1.0 / (1.0 + driver.getDistanceInKm());

                    double ratingScore =
                            getDummyRating(driver.getDriverId()) / 5.0;

                    return (distanceScore * 0.85)
                            + (ratingScore * 0.15);
                }));
    }

    private double getDummyRating(String driverId) {
        int value = Math.abs(driverId.hashCode()) % 50;
        return 4.5 + (value / 100.0);
    }
}
