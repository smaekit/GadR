package com.marcusjakobsson.gadr;



/**
 * Created by carlbratt on 2017-11-13.
 * Holds data of users.
 */

class UserData {

    private static final String TAG = "UserData";

    private String fbID;
    private String name;
    private String imgURLLarge;
    private String imgURLSmall;
    private String status = "";
    private double latitude;
    private double longitude;
    private boolean shareLocation = true;

    public UserData() {}

    public UserData(String fbID, String name, String imgURLLarge, String imgURLSmall, String status, double latitude, double longitude, boolean shareLocation){
        this.fbID = fbID;
        this.name = name;
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
                ", imgURLLarge='" + imgURLLarge + '\'' +
                ", imgURLSmall='" + imgURLSmall + '\'' +
                ", status='" + status + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
