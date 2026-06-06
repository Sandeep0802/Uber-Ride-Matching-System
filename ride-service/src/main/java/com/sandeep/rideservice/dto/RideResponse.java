package com.sandeep.rideservice.dto;

import com.sandeep.rideservice.model.RideStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


@Schema(description = "Ride details response")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideResponse {

    @Schema(example = "ride_123")
    private String id;

    @Schema(example = "rider:1")
    private String riderId;

    @Schema(example = "driver:2")
    private String driverId;


    private double pickupLatitude;
    private double pickupLongitude;
    private String pickupAddress;


    private double dropLatitude;
    private double dropLongitude;
    private String dropAddress;


    @Schema(example = "ACCEPTED")
    private RideStatus status;

    @Schema(example = "250.0")
    private double estimatedFare;

    @Schema(example = "275.0")
    private double actualFare;


    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}
