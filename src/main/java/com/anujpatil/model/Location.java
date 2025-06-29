package com.anujpatil.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.anujpatil.support.Distance;

@Document(collection = "locations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    @Id
    private String id; // MongoDB uses String-based ObjectId

    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    private Double longitude;

    @NotBlank(message = "Address is required")
    private String address;

    private String city;
    private String state;
    private String postalCode;
    private String country;

    // Haversine formula
    public double distanceTo(Location other) {
        System.out.println("Calculating distance between " + this + " and " + other);
        return Distance.getDrivingDistanceKm(this.latitude, this.longitude, other.latitude, other.longitude);
    }
}
