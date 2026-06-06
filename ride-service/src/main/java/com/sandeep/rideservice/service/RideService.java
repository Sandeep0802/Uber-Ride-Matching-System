package com.sandeep.rideservice.service;
import com.sandeep.rideservice.dto.RideRequest;
import com.sandeep.rideservice.dto.RideResponse;
import com.sandeep.rideservice.event.RideRequestedEvent;
import com.sandeep.rideservice.model.Ride;
import com.sandeep.rideservice.model.RideStatus;
import com.sandeep.rideservice.repository.RideRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class RideService {


    private final RideRepository rideRepository;
    private final KafkaTemplate<String, RideRequestedEvent> kafkaTemplate;
    private static final String RIDE_REQUESTED_TOPIC="ride.requested";

    public RideService(RideRepository rideRepository, KafkaTemplate<String, RideRequestedEvent> kafkaTemplate) {
        this.rideRepository = rideRepository;
        this.kafkaTemplate = kafkaTemplate;
    }


    //create ride in DB with Requested Status
    public RideResponse requestRide( RideRequest rideRequest) {

        log.info("New Ride request from rider: {}",rideRequest.getRiderId());

        //Step 1: Save ride to database
        Ride ride=new Ride();

        ride.setRiderId(rideRequest.getRiderId());
        ride.setPickupLatitude(rideRequest.getPickupLatitude());
        ride.setPickupLongitude(rideRequest.getPickupLongitude());
        ride.setPickupAddress(rideRequest.getPickupAddress());
        ride.setDropLatitude(rideRequest.getDropLatitude());
        ride.setDropLongitude(rideRequest.getDropLongitude());
        ride.setDropAddress(rideRequest.getDropAddress());
        ride.setStatus(RideStatus.REQUESTED);
        ride.setEstimatedFare(calculateEstimateFare(rideRequest));

        Ride savedRide=rideRepository.save(ride);

        //Step 2: Publish event to kafka
        //matching service will consume this and find requested driver

        RideRequestedEvent event=new RideRequestedEvent(
                savedRide.getId(),
                savedRide.getRiderId(),
                savedRide.getPickupLatitude(),
                savedRide.getPickupLongitude(),
                savedRide.getPickupAddress(),
                savedRide.getDropLatitude(),
                savedRide.getDropLongitude(),
                savedRide.getDropAddress()
        );

        kafkaTemplate.send(RIDE_REQUESTED_TOPIC,savedRide.getId(),event);
        log.info("RideRequestedEvent published to kafka for ride: {}",savedRide.getId());

        savedRide.setStatus(RideStatus.MATCHING);
        rideRepository.save(savedRide);

        return mapToResponse(savedRide);
    }

    //this method is not in controller beacuse
    //this is call by matching service after driver accept the ride
    //so that the status of ride change to ACCEPTED correctly

     public void updateRideWithDriver(String rideId,String driverId){

        Ride ride=rideRepository.findById(rideId).orElseThrow(()->new RuntimeException("Ride Not Found"));

        ride.setDriverId(driverId);
        ride.setStatus(RideStatus.ACCEPTED);

        rideRepository.save(ride);
     }


    public RideResponse startRide(String rideId) {

        Ride ride=rideRepository.findById(rideId).orElseThrow(()->new RuntimeException("Ride Not Found"));

        if(ride.getStatus() != RideStatus.ACCEPTED){
            throw new RuntimeException("Ride cant be started. Cuurent Status: "+ride.getStatus());
        }

        ride.setStatus(RideStatus.RIDE_STARTED);
        ride.setStartedAt(LocalDateTime.now());

        rideRepository.save(ride);

        return mapToResponse(ride);

    }

    public RideResponse completeRide(String rideId) {

        Ride ride=rideRepository.findById(rideId).orElseThrow(()->new RuntimeException("Ride Not Found"));

        if(ride.getStatus() != RideStatus.RIDE_STARTED){
            throw new RuntimeException("Ride cant be completed. Cuurent Status: "+ride.getStatus());
        }

        ride.setStatus(RideStatus.COMPLETED);
        ride.setCompletedAt(LocalDateTime.now());
        ride.setActualFare(ride.getEstimatedFare());

        rideRepository.save(ride);

        return mapToResponse(ride);

    }

    public RideResponse cancleRide(String rideId) {

        Ride ride=rideRepository.findById(rideId).orElseThrow(()->new RuntimeException("Ride Not Found"));

        if(ride.getStatus() == RideStatus.COMPLETED){
            throw new RuntimeException("Completed ride cannot be cancelled");
        }
        if(ride.getStatus() == RideStatus.RIDE_STARTED){
            throw new RuntimeException("Ride in progress cannot be cancelled");
        }

        ride.setStatus(RideStatus.CANCELLED);

        rideRepository.save(ride);

        return mapToResponse(ride);
    }

    public RideResponse getRideById(String rideId) {

        Ride ride=rideRepository.findById(rideId).orElseThrow(()->new RuntimeException("Ride Not Found"));

        return mapToResponse(ride);
    }

    public List<RideResponse> getRidesByRider(String riderId) {

        return rideRepository.findByRiderIdOrderByCreatedAtDesc(riderId)
                .stream()
                .map(this ::mapToResponse)
                .collect(Collectors.toList());
    }


    private double calculateEstimateFare(RideRequest request) {

        double lat1 = Math.toRadians(request.getPickupLatitude());
        double lat2 = Math.toRadians(request.getDropLatitude());

        double long1 = Math.toRadians(request.getPickupLongitude());
        double long2 = Math.toRadians(request.getDropLongitude());

        double dLat = lat2 - lat1;
        double dLong = long2 - long1;

        double a =
                Math.pow(Math.sin(dLat / 2), 2)
                        + Math.cos(lat1)
                        * Math.cos(lat2)
                        * Math.pow(Math.sin(dLong / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        double distanceKm = 6371 * c;

        // Base fare: ₹50 + ₹12/km
        double fare = 50 + (distanceKm * 12);

        return Math.round(fare * 100.0) / 100.0;
    }

    public RideResponse mapToResponse(Ride ride) {
        return new RideResponse(
                ride.getId(),
                ride.getRiderId(),
                ride.getDriverId(),
                ride.getPickupLatitude(),
                ride.getPickupLongitude(),
                ride.getPickupAddress(),
                ride.getDropLatitude(),
                ride.getDropLongitude(),
                ride.getDropAddress(),
                ride.getStatus(),
                ride.getEstimatedFare(),
                ride.getActualFare(),
                ride.getCreatedAt(),
                ride.getUpdatedAt(),
                ride.getStartedAt(),
                ride.getCompletedAt()
        );
    }



}
