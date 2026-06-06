package com.sandeep.rideservice.model;

//Flow
// REQUESTED->MATCHING->ACCEPTED->DRIVER_ARRIVING->RIDE STARTED->COMPLETED->CANCELLED(can happen at multiple stages)

public enum RideStatus {
    REQUESTED,
    MATCHING,
    ACCEPTED,
    DRIVER_ARRIVING,
    RIDE_STARTED,
    COMPLETED,
    CANCELLED
}
