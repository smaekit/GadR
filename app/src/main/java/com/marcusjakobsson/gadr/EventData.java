package com.marcusjakobsson.gadr;

import android.location.Location;

import java.util.Date;

/**
 * Created by carlbratt on 2017-11-13.
 */

public class EventData {

    private String cretorID;
    private String title;
    private String description;
//    private Location location;
    private Date date;

    public EventData() {}

    public EventData(String cretorID, String title, String description, Date date) {
        this.cretorID = cretorID;
        this.title = title;
        this.description = description;
        this.date = date;
    }

    /*public EventData(String cretorID, String title, String description, Location location, Date date) {
        this.cretorID = cretorID;
        this.title = title;
        this.description = description;
        this.location = location;
        this.date = date;
    }*/

    // Setters
    public void setCretorID(String cretorID) {
        this.cretorID = cretorID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

/*    public void setLocation(Location location) {
        this.location = location;
    }*/

    public void setDate(Date date) {
        this.date = date;
    }

    //  Getters
    public String getCretorID() {
        return cretorID;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

/*    public Location getLocation() {
        return location;
    }*/

    public Date getDate() {
        return date;
    }
}
