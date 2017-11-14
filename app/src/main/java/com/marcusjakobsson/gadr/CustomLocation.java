package com.marcusjakobsson.gadr;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by carlbratt on 2017-11-14.
 */

public class CustomLocation {

    private static final String TAG = "CustomLocation";

    private double latitude;
    private double longitude;

    public CustomLocation() {
        this.latitude = 57.7824464;
        this.longitude = 14.176048900000069; // Coordinates to JTH.
    }

    public CustomLocation(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public CustomLocation(LatLng latLng) {
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
    }

    public LatLng toLatLng() {
        return new LatLng(latitude,longitude);
    }


    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}


