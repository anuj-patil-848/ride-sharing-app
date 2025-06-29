package com.anujpatil.service;

import com.anujpatil.model.Location;
import com.anujpatil.model.Ride;
import com.anujpatil.model.RideStatus;
import com.anujpatil.model.User;
import com.anujpatil.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RideService {

    private final RideRepository rideRepository;

    @Autowired
    public RideService(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }

    @Transactional
    public Ride createRideRequest(User rider, Location pickupLocation, Location dropoffLocation,
                                  LocalDateTime requestedPickupTime) {
        Ride ride = Ride.builder()
                .rider(rider)
                .pickupLocation(pickupLocation)
                .dropoffLocation(dropoffLocation)
                .requestedPickupTime(requestedPickupTime)
                .status(RideStatus.REQUESTED)
                .build();

        ride.calculateFare();
        ride.initializeForCreate();
        return rideRepository.save(ride);
    }

    @Transactional
    public Optional<Ride> assignDriver(String rideId, User driver) {
        return rideRepository.findById(rideId)
                .filter(ride -> ride.getStatus() == RideStatus.REQUESTED)
                .map(ride -> {
                    ride.setDriver(driver);
                    ride.setStatus(RideStatus.ACCEPTED);
                    return rideRepository.save(ride);
                });
    }

    @Transactional
    public Optional<Ride> updateRideStatus(String rideId, RideStatus status) {
        return rideRepository.findById(rideId)
                .map(ride -> {
                    ride.setStatus(status);
                    if (status == RideStatus.IN_PROGRESS) {
                        ride.setActualPickupTime(LocalDateTime.now());
                    } else if (status == RideStatus.COMPLETED) {
                        ride.setCompletionTime(LocalDateTime.now());
                    }
                    return rideRepository.save(ride);
                });
    }

    @Transactional
    public Optional<Ride> cancelRide(String rideId, String cancelledBy) {
        return rideRepository.findById(rideId)
                .filter(ride -> ride.getStatus() != RideStatus.COMPLETED)
                .map(ride -> {
                    switch (cancelledBy.toLowerCase()) {
                        case "rider" -> ride.setStatus(RideStatus.CANCELLED_BY_RIDER);
                        case "driver" -> ride.setStatus(RideStatus.CANCELLED_BY_DRIVER);
                        default -> ride.setStatus(RideStatus.CANCELLED_BY_SYSTEM);
                    }
                    return rideRepository.save(ride);
                });
    }

    @Transactional
    public Optional<Ride> rateRide(String rideId, Integer rating, String comments, boolean isRiderRating) {
        return rideRepository.findById(rideId)
                .filter(ride -> ride.getStatus() == RideStatus.COMPLETED)
                .map(ride -> {
                    if (isRiderRating) {
                        ride.setRiderRating(rating);
                        ride.setRiderComments(comments);
                    } else {
                        ride.setDriverRating(rating);
                        ride.setDriverComments(comments);
                    }
                    return rideRepository.save(ride);
                });
    }

   /* public List<Ride> findPotentialRideShares(
            Location pickupLocation,
            Location dropoffLocation,
            LocalDateTime requestedPickupTime,
            double pickupRadiusKm,
            double dropoffRadiusKm,
            int timeWindowMinutes) {

        return rideRepository.findPotentialRideShares(
                pickupLocation.getLatitude(),
                pickupLocation.getLongitude(),
                dropoffLocation.getLatitude(),
                dropoffLocation.getLongitude(),
                requestedPickupTime,
                pickupRadiusKm,
                dropoffRadiusKm,
                timeWindowMinutes);
    }*/

    public Optional<Ride> getRideById(String rideId) {
        return rideRepository.findById(rideId);
    }

    public List<Ride> getRidesByRider(User rider) {
        return rideRepository.findByRider(rider);
    }

    public List<Ride> getRidesByDriver(User driver) {
        return rideRepository.findByDriver(driver);
    }

    public List<Ride> getRidesByStatus(RideStatus status) {
        return rideRepository.findByStatus(status);
    }

    public List<Ride> getNearbyRides(double latitude, double longitude, double searchRadiusKm) {
        Point location = new Point(longitude, latitude); // Note: GeoJSON uses [longitude, latitude]
        Distance radius = new Distance(searchRadiusKm, Metrics.KILOMETERS);
        return rideRepository.findByPickupGeoPointNear(location, radius);
    }

    public List<Ride> getNearbyPickUps(double latitude, double longitude, double searchRadiusKm) {
        Point location = new Point(longitude, latitude); // Note: GeoJSON uses [longitude, latitude]
        Distance radius = new Distance(searchRadiusKm, Metrics.KILOMETERS);
        return rideRepository.findByPickupGeoPointNear(location, radius);
    }

    public List<Ride> getNearbyDropOffs(double latitude, double longitude, double searchRadiusKm) {
        Point location = new Point(longitude, latitude); // Note: GeoJSON uses [longitude, latitude]
        Distance radius = new Distance(searchRadiusKm, Metrics.KILOMETERS);
        return rideRepository.findByDropoffGeoPointNear(location, radius);
    }
}
