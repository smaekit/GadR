package com.marcusjakobsson.gadr;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by carlbratt on 2017-11-13.
 */

public class UserData extends ArrayList<UserData> implements Parcelable{

    private static final String TAG = "UserData";

    private String fbID;
    private String name;
    private int points;
    private String imgURLLarge;
    private String imgURLSmall;
    private String status = "";
    private double latitude;
    private double longitude;
    private boolean shareLocation = true;

    public UserData() {}

    public UserData(String fbID, String name, int points, String imgURLLarge, String imgURLSmall, String status, double latitude, double longitude, boolean shareLocation){
        this.fbID = fbID;
        this.name = name;
        this.points = points;
        this.imgURLLarge = imgURLLarge;
        this.imgURLSmall = imgURLSmall;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
        this.shareLocation = shareLocation;
    }


    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }


    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    public void setStatus(String status) {
        this.status = status;
    }

    //  Setters
    public void setFbID(String fbID) {
        this.fbID = fbID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setImgURLLarge(String imgURLLarge) {
        this.imgURLLarge = imgURLLarge;
    }

    public void setImgURLSmall(String imgURLSmall) {
        this.imgURLSmall = imgURLSmall;
    }

    public void setShareLocation(Boolean shareLocation) {this.shareLocation = shareLocation; }


    //  Getters
    public String getFbID() {
        return fbID;
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public String getStatus() {
        return status;
    }

    public String getImgURLLarge() {
        return imgURLLarge;
    }

    public String getImgURLSmall() {
        return imgURLSmall;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean getShareLocation() { return shareLocation; }

    @Override
    public String toString() {
        return "UserData{" +
                "fbID='" + fbID + '\'' +
                ", name='" + name + '\'' +
                ", points=" + points +
                ", imgURLLarge='" + imgURLLarge + '\'' +
                ", imgURLSmall='" + imgURLSmall + '\'' +
                ", status='" + status + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(fbID);
        parcel.writeString(name);
        parcel.writeString(imgURLLarge);
        parcel.writeString(imgURLSmall);
        parcel.writeString(status);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeInt((shareLocation) ? 1 : 0);
    }

    public static final Parcelable.Creator<UserData> CREATOR
            = new Parcelable.Creator<UserData>() {
        public UserData createFromParcel(Parcel in) {
            return new UserData(in);
        }

        public UserData[] newArray(int size) {
            return new UserData[size];
        }
    };

    public UserData(Parcel in) {
        fbID = in.readString();
        name = in.readString();
        imgURLLarge = in.readString();
        imgURLSmall = in.readString();
        status = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        shareLocation = (in.readInt() == 1) ? true : false;
    }
}
