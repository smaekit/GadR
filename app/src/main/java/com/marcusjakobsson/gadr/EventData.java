package com.marcusjakobsson.gadr;

import android.location.Location;

import java.util.Date;

/**
 * Created by carlbratt on 2017-11-13.
 * Contains data for Events.
 */

public class EventData {

    private static final String TAG = "EventData";

    public static final String DATE_FORMAT_STRING = "dd/MM - yy";
    public static final String TIME_FORMAT_STRING = "HH:mm";

    private String creatorID;
    private String title;
    private String description;

    private CustomLocation customLocation;
    private String locationNickname;
    private int categoryIndex;

    private String date;
    private String startTime;
    private String endTime;

    public EventData() {}

    public EventData(
            String creatorID,
            String title,
            String description,
            CustomLocation customLocation,
            String locationNickname,
            int categoryIndex,
            String date,
            String startTime,
            String endTime) {

        this.creatorID = creatorID;
        this.title = title;
        this.description = description;

        this.customLocation = customLocation;
        this.locationNickname = locationNickname;
        this.categoryIndex = categoryIndex;

        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Setters
    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCustomLocation(CustomLocation customLocation) { this.customLocation = customLocation; }

    public void setLocationNickname(String locationNickname) { this.locationNickname = locationNickname; }

    public void setCategoryIndex(int categoryIndex) { this.categoryIndex = categoryIndex; }

    public void setDate(String date) { this.date = date; }

    public void setStartTime(String startTime) { this.startTime = startTime; }

    public void setEndTime(String endTime) { this.endTime = endTime; }

    //  Getters
    public String getCreatorID() {
        return creatorID;
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

    public String getLocationNickname() { return locationNickname; }

    public int getCategoryIndex() { return categoryIndex; }

    public String getDate() {
        return date;
    }

    public String getStartTime() { return startTime; }

    public String getEndTime() { return endTime; }

    @Override
    public String toString() {
        return "EventData{" +
                "creatorID='" + creatorID + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", customLocation=" + customLocation +
                ", locationNickname=" + locationNickname +
                ", categoryIndex=" + categoryIndex +
                ", date='" + date + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
