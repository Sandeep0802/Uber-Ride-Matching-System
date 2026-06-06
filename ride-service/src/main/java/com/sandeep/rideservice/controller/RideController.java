package com.sandeep.rideservice.controller;

import com.sandeep.rideservice.dto.RideRequest;
import com.sandeep.rideservice.dto.RideResponse;
import com.sandeep.rideservice.service.RideService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Ride Management",
        description = "Manage ride requests and ride lifecycle operations"
)
@RestController
@RequestMapping("/api/v1/rides")
@Slf4j
public class RideController {

    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @Operation(
            summary = "Request a Ride",
            description = "Creates a new ride request and publishes a ride.requested event to Kafka."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ride created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ride request")
    })
    @PostMapping("/request")
    public ResponseEntity<RideResponse> rideRequest(
            @Valid @RequestBody RideRequest rideRequest){

        log.info("Ride request received from rider: {} ",rideRequest.getRiderId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(rideService.requestRide(rideRequest));
    }

    @Operation(
            summary = "Get Ride By ID",
            description = "Returns complete ride details using ride id."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ride found"),
            @ApiResponse(responseCode = "404", description = "Ride not found")
    })
    @GetMapping("/{rideId}")
    public ResponseEntity<RideResponse> getRideById(
            @Parameter(
                    description = "Unique ride identifier",
                    example = "ride_123"
            )
            @PathVariable String rideId){

        return ResponseEntity.ok(rideService.getRideById(rideId));
    }

    @Operation(
            summary = "Get Rider Ride History",
            description = "Returns all rides associated with a specific rider."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ride history fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Rider not found")
    })
    @GetMapping("/rider/{riderId}")
    public ResponseEntity<List<RideResponse>> getRidesByRider(

            @Parameter(
                    description = "Unique rider identifier",
                    example = "rider:1"
            )
            @PathVariable String riderId){

        return ResponseEntity.ok(rideService.getRidesByRider(riderId));
    }

    @Operation(
            summary = "Start Ride",
            description = "Changes ride status from ACCEPTED to STARTED."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ride started successfully"),
            @ApiResponse(responseCode = "404", description = "Ride not found"),
            @ApiResponse(responseCode = "400", description = "Ride cannot be started")
    })
    @PutMapping("/{rideId}/start")
    public ResponseEntity<RideResponse> startRide(

            @Parameter(
                    description = "Unique ride identifier",
                    example = "ride_123"
            )
            @PathVariable String rideId){

        return ResponseEntity.ok(rideService.startRide(rideId));
    }

    @Operation(
            summary = "Complete Ride",
            description = "Changes ride status from STARTED to COMPLETED."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ride completed successfully"),
            @ApiResponse(responseCode = "404", description = "Ride not found"),
            @ApiResponse(responseCode = "400", description = "Ride cannot be completed")
    })
    @PutMapping("/{rideId}/complete")
    public ResponseEntity<RideResponse> completeRide(

            @Parameter(
                    description = "Unique ride identifier",
                    example = "ride_123"
            )
            @PathVariable String rideId){

        return ResponseEntity.ok(rideService.completeRide(rideId));
    }

    @Operation(
            summary = "Cancel Ride",
            description = "Cancels an existing ride."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ride cancelled successfully"),
            @ApiResponse(responseCode = "404", description = "Ride not found"),
            @ApiResponse(responseCode = "400", description = "Ride cannot be cancelled")
    })
    @PutMapping("/{rideId}/cancle")
    public ResponseEntity<RideResponse> cancleRide(

            @Parameter(
                    description = "Unique ride identifier",
                    example = "ride_123"
            )
            @PathVariable String rideId){

        return ResponseEntity.ok(rideService.cancleRide(rideId));
    }
}