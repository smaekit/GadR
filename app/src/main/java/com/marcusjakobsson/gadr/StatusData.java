package com.marcusjakobsson.gadr;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/**
 * Created by carlbratt on 2017-11-13.
 */

public class StatusData {

    private static final String TAG = "StatusData";

    private String creatorID;
    private String status;
    private CustomLocation customLocation;
    private Date date;

    public StatusData() {}

    public StatusData(String creatorID, String status, CustomLocation customLocation, Date date){
        this.creatorID = creatorID;
        this.status = status;
        this.customLocation = customLocation;
        this.date = date;
    }

    //  Setters
    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCustomLocation(CustomLocation customLocation) {
        this.customLocation = customLocation;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    //  Getters
    public String getCreatorID() {
        return creatorID;
    }

    public String getStatus() {
        return status;
    }

    public CustomLocation getCustomLocation() {
        return customLocation;
    }

    public Date getDate() {
        return date;
    }
}
