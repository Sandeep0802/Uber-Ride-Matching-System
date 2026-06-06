package com.sandeep.locationservice.controller;

import com.sandeep.locationservice.dto.DriverLocationRequest;
import com.sandeep.locationservice.dto.NearByDriverResponse;
import com.sandeep.locationservice.service.LocationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

//for swagger ui
@Tag(
        name = "Location Management",
        description = "Track and search driver locations"
)


@RestController
@RequestMapping("/api/v1/locations")
@Slf4j

public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }


    @Operation(
            summary = "Update Driver Location",
            description = "Called by driver's phone periodically to update current location in Redis Geospatial."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Driver location updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    //drivers phone update their location every 3 sec
    @PostMapping("/drivers/update")
    public ResponseEntity<String> updateDriverLocation(
            @RequestBody DriverLocationRequest driverLocationRequest){

        locationService.updateDriverLocation(driverLocationRequest);
        return ResponseEntity.ok("Driver Location Updated");
    }



    @Operation(
            summary = "Find Nearby Drivers",
            description = "Returns all drivers within the specified radius from the pickup location."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Nearby drivers found",
                    content = @Content(schema = @Schema(implementation = NearByDriverResponse.class))
            )
    })
    //matching service calls this when ride is requested
    @GetMapping("/drivers/nearby")
    public ResponseEntity<List<NearByDriverResponse>> getNearByDrivers(

            @Parameter(description = "Pickup longitude", example = "77.5946")
            @RequestParam double longitude,

            @Parameter(description = "Pickup latitude", example = "12.9716")
            @RequestParam double latitude,

            @Parameter(description = "Search radius in kilometers", example = "5")
            @RequestParam(defaultValue = "5.0") double radiusInKm){

        return ResponseEntity.ok(
                locationService.findNearbyDrivers(
                        longitude,
                        latitude,
                        radiusInKm
                )
        );
    }


    @Operation(
            summary = "Remove Driver",
            description = "Removes driver location from Redis when driver goes offline."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Driver removed successfully"),
            @ApiResponse(responseCode = "404", description = "Driver not found")
    })
    //when driver goes offline
    @DeleteMapping("/drivers/{driverId}")
    public ResponseEntity<String> removeDriver(

            @Parameter(
                    description = "Unique driver identifier",
                    example = "driver:1"
            )
            @PathVariable String driverId){

        locationService.removeDriver(driverId);
        return ResponseEntity.ok("Driver removed successfully");
    }
}
