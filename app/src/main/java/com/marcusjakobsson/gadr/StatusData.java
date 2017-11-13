package com.marcusjakobsson.gadr;

import android.location.Location;

/**
 * Created by carlbratt on 2017-11-13.
 */

public class StatusData {

    private String creatorID;
    private String status;
    private Location location;


    public StatusData(String creatorID, String status, Location location){
        this.creatorID = creatorID;
        this.status = status;
        this.location = location;
    }


    //  Setters
    public void setFbID(String creatorID) {
        this.creatorID = creatorID;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setLocation(Location location) {
        this.location = location;
    }


    //  Getters
    public String getFbID() {
        return creatorID;
    }

    public String getStatus() {
        return status;
    }

    public Location getLocation() {
        return location;
    }
}
