package com.marcusjakobsson.gadr;



/**
 * Created by carlbratt on 2017-11-13.
 */

public class UserData {

    private static final String TAG = "UserData";

    private String fbID;
    private String name;
    private int points;
    private String imgURLLarge;
    private String imgURLSmall;
    private String status = "";
    private double latitude;
    private double longitude;

    public UserData() {}

    public UserData(String fbID, String name, int points, String imgURLLarge, String imgURLSmall, String status, double latitude, double longitude){
        this.fbID = fbID;
        this.name = name;
        this.points = points;
        this.imgURLLarge = imgURLLarge;
        this.imgURLSmall = imgURLSmall;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getStatus() {
        return status;
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

    public String getImgURLLarge() {
        return imgURLLarge;
    }

    public String getImgURLSmall() {
        return imgURLSmall;
    }

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
}
