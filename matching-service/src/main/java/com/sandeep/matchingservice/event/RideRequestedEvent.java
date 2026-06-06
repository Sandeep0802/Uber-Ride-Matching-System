package com.sandeep.matchingservice.event;


  //Event is published to kafka topic by ride service when a rider requested for ride
  //Event consumed by matching service from kafta topic:ride.requested

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideRequestedEvent {

    private String rideId;
    private String riderId;

    //PICKUP
    private double pickupLatitude;
    private double pickupLongitude;
    private String pickupAddress;

    //DROP
    private double dropLatitude;
    private double dropLongitude;
    private String dropAddress;
}
