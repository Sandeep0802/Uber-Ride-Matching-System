package com.sandeep.matchingservice.dto;


//Response received from the location service
//when querying for nearby drivers

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NearByDriverResponse {

    private String driverId;
    private double latitude;
    private double longitude;
    private double distanceInKm;
}
