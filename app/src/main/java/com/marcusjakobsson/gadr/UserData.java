package com.marcusjakobsson.gadr;



/**
 * Created by carlbratt on 2017-11-13.
 */

public class UserData {

    private String fbID;
    private String name;
    private int points;
    private String imgURL_large;
    private String imgURL_small;

    public UserData(String fbID, String name, int points, String imgURL_large, String imgURL_small){
        this.fbID = fbID;
        this.name = name;
        this.points = points;
        this.imgURL_large = imgURL_large;
        this.imgURL_small = imgURL_small;
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

    public void setImgURL_large(String imgURL) {
        this.imgURL_large = imgURL;
    }

    public void setImgURL_small(String imgURL) {
        this.imgURL_small = imgURL;
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

    public String getImgURL_large() {
        return imgURL_large;
    }

    public String getImgURL_small() {
        return imgURL_small;
    }
}
