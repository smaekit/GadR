package com.marcusjakobsson.gadr;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.List;

/**
 * Created by carlbratt on 2017-11-13.
 */

public class EventData implements Parcelable{

    private static final String TAG = "EventData";

    public static final String DATE_FORMAT_STRING = "dd/MM - yy";
    public static final String TIME_FORMAT_STRING = "HH:mm";

    private String creatorID;
    private String title;
    private String description;
    private CustomLocation customLocation;
    //private Date date;

    private String date;
    private String startTime;
    private String endTime;

    public EventData() {}

    public EventData(String creatorID, String title, String description, CustomLocation customLocation, String date, String startTime, String endTime) {
        this.creatorID = creatorID;
        this.title = title;
        this.description = description;
        this.customLocation = customLocation;

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
                ", date='" + date + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeDouble(customLocation.getLatitude());
        parcel.writeDouble(customLocation.getLongitude());
        parcel.writeString(date);
        parcel.writeString(startTime);
        parcel.writeString(endTime);
    }


    public static final Parcelable.Creator<EventData> CREATOR
            = new Parcelable.Creator<EventData>() {
        public EventData createFromParcel(Parcel in) {
            return new EventData(in);
        }

        public EventData[] newArray(int size) {
            return new EventData[size];
        }

    };

    public EventData(Parcel in) {
        title = in.readString();
        description = in.readString();
        customLocation.setLatitude(in.readDouble());
        customLocation.setLongitude(in.readDouble());
        date = in.readString();
        startTime = in.readString();
        endTime = in.readString();
    }
}
