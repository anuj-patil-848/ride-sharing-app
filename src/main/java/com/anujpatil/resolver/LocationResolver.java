package com.anujpatil.resolver;

import com.anujpatil.model.Location;
import com.anujpatil.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class LocationResolver {

    private final LocationService locationService;

    @Autowired
    public LocationResolver(LocationService locationService) {
        this.locationService = locationService;
    }

    // Query Resolvers

    @QueryMapping
    public Optional<Location> location(@Argument String id) {
        Optional<Location> result =  locationService.getLocationById(id);
        if(result.isEmpty()) {
            throw new RuntimeException("No location found for id: " + id);
        }
        return result;
    }

    @QueryMapping
    public List<Location> locationsByCity(@Argument String city) {
        List<Location> result =  locationService.getLocationsByCity(city);
        if(result.isEmpty()) {
            throw new RuntimeException("No locations found for city: " + city);
        }
        return result;
    }

    @QueryMapping
    public List<Location> locationsByPostalCode(@Argument String postalCode) {
        List<Location> result = locationService.getLocationsByPostalCode(postalCode);
        if(result.isEmpty()) {
            throw new RuntimeException("No locations found for postal code: " + postalCode);
        }
        return result;
    }

    /*@QueryMapping
    public List<Location> locationsWithinRadius(
            @Argument Double latitude,
            @Argument Double longitude,
            @Argument Double radiusKm) {
        return locationService.findLocationsWithinRadius(latitude, longitude, radiusKm);
    }*/

    @QueryMapping
    public List<Location> allLocations() {
        return locationService.getAllLocations();
    }

    @MutationMapping
    public Location createLocation(@Argument("input") Map<String, Object> input) {
        return locationService.createLocation(mapToLocation(input));
    }

    @MutationMapping
    public Optional<Location> updateLocation(@Argument String id, @Argument("input") Map<String, Object> input) {
        return locationService.updateLocation(id, mapToLocation(input));
    }

    private Location mapToLocation(Map<String, Object> input) {
        return Location.builder()
                .latitude(castToDouble(input.get("latitude")))
                .longitude(castToDouble(input.get("longitude")))
                .address((String) input.get("address"))
                .city((String) input.get("city"))
                .state((String) input.get("state"))
                .postalCode((String) input.get("postalCode"))
                .country((String) input.get("country"))
                .build();
    }

    private Double castToDouble(Object obj) {
        return obj instanceof Number ? ((Number) obj).doubleValue() : null;
    }
}
