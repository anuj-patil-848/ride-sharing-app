package com.anujpatil.resolver;

import com.anujpatil.model.Location;
import com.anujpatil.model.Ride;
import com.anujpatil.model.RideStatus;
import com.anujpatil.model.User;
import com.anujpatil.service.LocationService;
import com.anujpatil.service.RideService;
import com.anujpatil.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class RideResolver {

    private final RideService rideService;
    private final UserService userService;
    private final LocationService locationService;

    @Autowired
    public RideResolver(RideService rideService, UserService userService, LocationService locationService) {
        this.rideService = rideService;
        this.userService = userService;
        this.locationService = locationService;
    }

    @QueryMapping
    public Optional<Ride> ride(@Argument String id) {
        return rideService.getRideById(id);
    }

    @QueryMapping
    public List<Ride> ridesByRider(@Argument String riderId) {
        return userService.getUserById(riderId)
                .map(rideService::getRidesByRider)
                .orElse(List.of());
    }

    @QueryMapping
    public List<Ride> ridesByDriver(@Argument String driverId) {
        return userService.getUserById(driverId)
                .map(rideService::getRidesByDriver)
                .orElse(List.of());
    }

    @QueryMapping
    public List<Ride> ridesByStatus(@Argument RideStatus status) {
        return rideService.getRidesByStatus(status);
    }

    @QueryMapping
    public List<Ride> ridesNearby(
            @Argument Double latitude,
            @Argument Double longitude,
            @Argument Optional<Double> radiusKm
    ) {
        double searchRadius = radiusKm.orElse(5.0); // default to 5 km if not provided
        return rideService.getNearbyRides(latitude, longitude, searchRadius);
    }

    @QueryMapping
    List<Ride> similarRides(@Argument Double pickUpLat, @Argument Double pickUpLon, @Argument Optional<Double> pickUpRadiusKm, @Argument Double dropOffLat, @Argument Double dropOffLon, @Argument Optional<Double> dropOffRadiusKm){
        double pickUpSearchRadius = pickUpRadiusKm.orElse(5.0);
        double dropOffSearchRadius = dropOffRadiusKm.orElse(5.0);
        List<Ride> nearbyPickUps = rideService.getNearbyPickUps(pickUpLat, pickUpLon, pickUpSearchRadius);
        List<Ride> nearbyDropOffs = rideService.getNearbyDropOffs(dropOffLat, dropOffLon, dropOffSearchRadius);

        List<Ride> result = new ArrayList<>();

        for (Ride nearbyPickUp : nearbyPickUps) {
            if (nearbyDropOffs.contains(nearbyPickUp)) {
                result.add(nearbyPickUp);
            }
        }

        return result;
    }

    /*@QueryMapping
    public List<Ride> potentialRideShares(
            @Argument Double pickupLatitude,
            @Argument Double pickupLongitude,
            @Argument Double dropoffLatitude,
            @Argument Double dropoffLongitude,
            @Argument LocalDateTime pickupTime,
            @Argument Double pickupRadiusKm,
            @Argument Double dropoffRadiusKm,
            @Argument Integer timeWindowMinutes) {

        Location pickupLocation = Location.builder()
                .latitude(pickupLatitude)
                .longitude(pickupLongitude)
                .address("Temporary Pickup")
                .build();

        Location dropoffLocation = Location.builder()
                .latitude(dropoffLatitude)
                .longitude(dropoffLongitude)
                .address("Temporary Dropoff")
                .build();

        return rideService.findPotentialRideShares(
                pickupLocation,
                dropoffLocation,
                pickupTime,
                pickupRadiusKm,
                dropoffRadiusKm,
                timeWindowMinutes
        );
    }*/

    @MutationMapping
    public Ride createRideRequest(@Argument("input") Map<String, Object> input) {
        String riderId = (String) input.get("riderId");
        String pickupLocationId = (String) input.get("pickupLocationId");
        String dropoffLocationId = (String) input.get("dropoffLocationId");

        Object requestedTimeObj = input.get("requestedPickupTime");
        LocalDateTime requestedPickupTime;

        if (requestedTimeObj instanceof String) {
            requestedPickupTime = LocalDateTime.parse((String) requestedTimeObj);
        } else if (requestedTimeObj instanceof LocalDateTime) {
            requestedPickupTime = (LocalDateTime) requestedTimeObj;
        } else {
            requestedPickupTime = LocalDateTime.now().plusMinutes(15);
        }

        User rider = userService.getUserById(riderId)
                .orElseThrow(() -> new IllegalArgumentException("Rider not found"));

        Location pickupLocation = locationService.getLocationById(pickupLocationId)
                .orElseThrow(() -> new IllegalArgumentException("Pickup location not found"));

        Location dropoffLocation = locationService.getLocationById(dropoffLocationId)
                .orElseThrow(() -> new IllegalArgumentException("Dropoff location not found"));

        return rideService.createRideRequest(rider, pickupLocation, dropoffLocation, requestedPickupTime);
    }


    @MutationMapping
    public Optional<Ride> assignDriver(@Argument String rideId, @Argument String driverId) {
        return userService.getUserById(driverId)
                .flatMap(driver -> rideService.assignDriver(rideId, driver));
    }

    @MutationMapping
    public Optional<Ride> updateRideStatus(@Argument String rideId, @Argument RideStatus status) {
        return rideService.updateRideStatus(rideId, status);
    }

    @MutationMapping
    public Optional<Ride> cancelRide(@Argument String rideId, @Argument String cancelledBy) {
        return rideService.cancelRide(rideId, cancelledBy);
    }

    @MutationMapping
    public Optional<Ride> rateRide(@Argument("input") Map<String, Object> input) {
        String rideId = (String) input.get("rideId");
        Integer rating = (Integer) input.get("rating");
        String comments = (String) input.get("comments");
        Boolean isRiderRating = (Boolean) input.get("isRiderRating");

        return rideService.rateRide(rideId, rating, comments, isRiderRating);
    }

    @SchemaMapping(typeName = "Ride", field = "rider")
    public User getRider(Ride ride) {
        return ride.getRider();
    }

    @SchemaMapping(typeName = "Ride", field = "driver")
    public Optional<User> getDriver(Ride ride) {
        return Optional.ofNullable(ride.getDriver());
    }

    @SchemaMapping(typeName = "Ride", field = "pickupLocation")
    public Location getPickupLocation(Ride ride) {
        return ride.getPickupLocation();
    }

    @SchemaMapping(typeName = "Ride", field = "dropoffLocation")
    public Location getDropoffLocation(Ride ride) {
        return ride.getDropoffLocation();
    }

    @SchemaMapping(typeName = "Ride", field = "parentRide")
    public Optional<Ride> getParentRide(Ride ride) {
        return Optional.ofNullable(ride.getParentRide());
    }
}
