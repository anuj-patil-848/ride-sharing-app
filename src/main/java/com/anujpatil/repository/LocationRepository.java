package com.anujpatil.repository;

import com.anujpatil.model.Location;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends MongoRepository<Location, String> {

    List<Location> findByCityIgnoreCase(String city);

    List<Location> findByPostalCode(String postalCode);

   // List<Location> findLocationsWithinRadius(double latitude, double longitude, double radiusKm);
}
