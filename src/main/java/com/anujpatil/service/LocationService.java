package com.anujpatil.service;

import com.anujpatil.model.Location;
import com.anujpatil.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    @Autowired
    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public Location createLocation(Location location) {
        return locationRepository.save(location);
    }

    public Optional<Location> updateLocation(String locationId, Location updatedLocation) {
        return locationRepository.findById(locationId)
                .map(existing -> {
                    if (updatedLocation.getLatitude() != null) existing.setLatitude(updatedLocation.getLatitude());
                    if (updatedLocation.getLongitude() != null) existing.setLongitude(updatedLocation.getLongitude());
                    if (updatedLocation.getAddress() != null) existing.setAddress(updatedLocation.getAddress());
                    if (updatedLocation.getCity() != null) existing.setCity(updatedLocation.getCity());
                    if (updatedLocation.getState() != null) existing.setState(updatedLocation.getState());
                    if (updatedLocation.getPostalCode() != null) existing.setPostalCode(updatedLocation.getPostalCode());
                    if (updatedLocation.getCountry() != null) existing.setCountry(updatedLocation.getCountry());
                    return locationRepository.save(existing);
                });
    }

    public Optional<Location> getLocationById(String locationId) {
        return locationRepository.findById(locationId);
    }

    public List<Location> getLocationsByCity(String city) {
        System.out.println("Looking for city: " + city);
        return locationRepository.findByCityIgnoreCase(city);
    }

    public List<Location> getLocationsByPostalCode(String postalCode) {
        return locationRepository.findByPostalCode(postalCode);
    }

   // public List<Location> findLocationsWithinRadius(double latitude, double longitude, double radiusKm) {
   //     return locationRepository.findLocationsWithinRadius(latitude, longitude, radiusKm);
   // }

    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    public double calculateDistance(Location location1, Location location2) {
        return location1.distanceTo(location2);
    }
}
