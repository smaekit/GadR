package com.marcusjakobsson.gadr;

import android.location.Location;

import java.util.Date;

/**
 * Created by carlbratt on 2017-11-13.
 */

public class EventData {

    private static final String TAG = "EventData";

    private String cretorID;
    private String title;
    private String description;
    private CustomLocation customLocation;
    //private Date date;

    private String date;
    private String startTime;
    private String endTime;

    public EventData() {}

    public EventData(String cretorID, String title, String description, CustomLocation customLocation, String date, String startTime, String endTime) {
        this.cretorID = cretorID;
        this.title = title;
        this.description = description;
        this.customLocation = customLocation;

        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

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

    public void setCustomLocation(CustomLocation customLocation) { this.customLocation = customLocation; }

    public void setDate(String date) { this.date = date; }

    public void setStartTime(String startTime) { this.startTime = startTime; }

    public void setEndTime(String endTime) { this.endTime = endTime; }

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

    public CustomLocation getCustomLocation() {
        return customLocation;
    }

    public String getDate() {
        return date;
    }

    public String getStartTime() { return startTime; }

    public String getEndTime() { return endTime; }

    @Override
    public String toString() {
        return "EventData{" +
                "cretorID='" + cretorID + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", customLocation=" + customLocation +
                ", date='" + date + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
