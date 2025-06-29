package com.anujpatil.repository;

import com.anujpatil.model.Ride;
import com.anujpatil.model.RideStatus;
import com.anujpatil.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RideRepository extends MongoRepository<Ride, String> {

    List<Ride> findByRider(User rider);

    List<Ride> findByDriver(User driver);

    List<Ride> findByStatus(RideStatus status);

    List<Ride> findByRiderAndStatus(User rider, RideStatus status);

    List<Ride> findByDriverAndStatus(User driver, RideStatus status);

    List<Ride> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Ride> findByPickupGeoPointNear(Point location, Distance radius);
    List<Ride> findByDropoffGeoPointNear(Point location, Distance radius);
}
