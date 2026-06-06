package com.sandeep.locationservice.service;


import com.sandeep.locationservice.dto.DriverLocationRequest;
import com.sandeep.locationservice.dto.NearByDriverResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class LocationService {


    //redis key for all drivers location
    private static final String DRIVERS_GEO_KEY="drivers:location";

    private final RedisTemplate<String,String> redisTemplate;

    public LocationService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    //update driver location in redis
    //called every 3 sec
    //maps to redis GEOADD command

    public void updateDriverLocation(DriverLocationRequest driverLocationRequest) {

         log.info("Updating location for driver: {} ",driverLocationRequest.getDriverId());

        Point driverPoint=new Point(
                driverLocationRequest.getLongitude(),
                driverLocationRequest.getLatitude()
        );

        //opsForGeo gives us all redis geo commands for adding,searching etc in redis
        redisTemplate.opsForGeo().add(
                DRIVERS_GEO_KEY,
                driverPoint,
                driverLocationRequest.getDriverId()
        );

        log.info("Location Updated for driver: {}",driverLocationRequest.getDriverId());
    }

   //find nearby driver within given range
    //called by Matching service as ride requested
    //maps to redis GEORADIUS command

    public List<NearByDriverResponse> findNearbyDrivers(double longitude, double latitude, double radiusInKm) {

        log.info("Finding driver near long: {} lat: {} within {} km", longitude, latitude, radiusInKm);

        Circle searchArea = new Circle(
                new Point(longitude, latitude),
                new Distance(radiusInKm, Metrics.KILOMETERS)
        );

        GeoResults<RedisGeoCommands.GeoLocation<String>> result =
                redisTemplate.opsForGeo().radius(
                        DRIVERS_GEO_KEY,
                        searchArea,
                        RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                                .includeCoordinates()
                                .includeDistance()
                                .sortAscending()
                                .limit(10)

                );

        List<NearByDriverResponse> nearbyDrivers = new ArrayList<>();

        if (result != null) {

            result.getContent().forEach(geoResult -> {

                RedisGeoCommands.GeoLocation<String> location =
                        geoResult.getContent();

                nearbyDrivers.add(new NearByDriverResponse(
                        location.getName(),
                        location.getPoint().getY(),
                        location.getPoint().getX(),
                        geoResult.getDistance().getValue()
                ));
            });

        }

        log.info("Found {} drivers nearby",nearbyDrivers.size());

        return nearbyDrivers;
    }


    public void removeDriver(String driverId) {

           log.info("Removing driver: {}",driverId);
           redisTemplate.opsForGeo().remove(DRIVERS_GEO_KEY,driverId);
    }
}
