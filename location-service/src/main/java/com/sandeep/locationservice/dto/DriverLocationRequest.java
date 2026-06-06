package com.sandeep.locationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Driver location update request")
public class DriverLocationRequest {

    @Schema(
            description = "Unique driver identifier",
            example = "driver:1"
    )
    private String driverId;

    @Schema(
            description = "Driver latitude",
            example = "12.9716"
    )
    private double latitude;

    @Schema(
            description = "Driver longitude",
            example = "77.5946"
    )
    private double longitude;
}
