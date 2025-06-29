package com.anujpatil.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "rides")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ride {

    @Id
    private String id;

    @NotNull(message = "Rider is required")
    private User rider;

    private User driver;

    @NotNull(message = "Pickup location is required")
    private Location pickupLocation;

    @NotNull(message = "Dropoff location is required")
    private Location dropoffLocation;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint pickupGeoPoint;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint dropoffGeoPoint;

    private RideStatus status;

    private Double distance;
    private Double fare;

    private LocalDateTime requestedPickupTime;
    private LocalDateTime actualPickupTime;
    private LocalDateTime completionTime;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Integer riderRating;
    private Integer driverRating;

    private String riderComments;
    private String driverComments;

    private Ride parentRide;

    public void initializeForCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = RideStatus.REQUESTED;
        }

        if (this.pickupLocation != null) {
            this.pickupGeoPoint = new GeoJsonPoint(
                    pickupLocation.getLongitude(),
                    pickupLocation.getLatitude()
            );
        }

        if(this.dropoffLocation != null){
            this.dropoffGeoPoint = new GeoJsonPoint(
                    dropoffLocation.getLongitude(),
                    dropoffLocation.getLatitude()
            );
        }
    }

    public void markUpdated() {
        this.updatedAt = LocalDateTime.now();
    }

    public void calculateFare() {
        if (distance == null && pickupLocation != null && dropoffLocation != null) {
            distance = pickupLocation.distanceTo(dropoffLocation);
        }

        double baseFare = 2.50;
        double ratePerKm = 1.25;
        fare = baseFare + (distance * ratePerKm);
        fare = Math.round(fare * 100.0) / 100.0;
    }
}
