package com.sandeep.rideservice.repository;

import com.sandeep.rideservice.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RideRepository extends JpaRepository<Ride,String> {

    List<Ride> findByRiderIdOrderByCreatedAtDesc(String riderId);
}
