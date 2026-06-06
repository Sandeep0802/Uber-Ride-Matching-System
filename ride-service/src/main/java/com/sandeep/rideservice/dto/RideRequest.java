package com.sandeep.rideservice.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Schema(description = "Ride creation request")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideRequest {

    @Schema(example = "rider:1")
    @NotBlank(message = "Rider id is required")
     private String riderId;

    @Schema(example = "12.9716")
    @NotNull(message = "Pickup latitude is required")
     private double pickupLatitude;

    @Schema(example = "77.5946")
    @NotNull(message = "Pickup longitude is required")
     private double pickupLongitude;

    @Schema(example = "MG Road, Bangalore")
    @NotNull(message = "Pickup address is required")
     private String pickupAddress;

    @Schema(example = "12.9352")
    @NotNull(message = "Drop latitude is required")
     private double dropLatitude;

    @Schema(example = "77.6245")
    @NotNull(message = "Drop longitude is required")
     private double dropLongitude;

    @Schema(example = "Koramangala, Bangalore")
    @NotNull(message = "Drop address is required")
     private String dropAddress;
}
