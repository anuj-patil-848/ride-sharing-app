package com.anujpatil.support;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;

public class Distance {

    private static final String API_KEY = "AIzaSyC47e3ErXVFuMYXLZMK4GlXvUNToeFgB2s";

    private static final GeoApiContext context = new GeoApiContext.Builder()
            .apiKey(API_KEY)
            .build();

    public static double getDrivingDistanceKm(double originLat, double originLng, double destLat, double destLng) {
        try {
            LatLng origin = new LatLng(originLat, originLng);
            LatLng destination = new LatLng(destLat, destLng);

            DistanceMatrix result = DistanceMatrixApi.newRequest(context)
                    .origins(origin)
                    .destinations(destination)
                    .mode(TravelMode.DRIVING)
                    .units(com.google.maps.model.Unit.METRIC)
                    .await();

            return result.rows[0].elements[0].distance.inMeters / 1000.0; // km
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // return -1 to indicate failure
        }
    }
}
