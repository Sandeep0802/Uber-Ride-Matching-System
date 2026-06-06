package com.sandeep.locationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Nearby driver information")
public class NearByDriverResponse {

    @Schema(example = "driver:1")
    private String driverId;

    @Schema(example = "12.9716")
    private double latitude;

    @Schema(example = "77.5946")
    private double longitude;

    @Schema(example = "1.25")
    private double distanceInKm;
}
