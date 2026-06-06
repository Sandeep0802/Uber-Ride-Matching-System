package com.sandeep.rideservice.event;

//Event is published to kafka when a ride is requested
//matching service will consume this event
//TOPIC : ride.requested


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
