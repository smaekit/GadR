package com.marcusjakobsson.gadr;



/**
 * Created by carlbratt on 2017-11-13.
 */

public class UserData {

    private String fbID;
    private String name;
    private int points;
    private String imgurlLarge;
    private String imgURLSmall;

    public UserData() {}

    public UserData(String fbID, String name, int points, String imgurlLarge, String imgURLSmall){
        this.fbID = fbID;
        this.name = name;
        this.points = points;
        this.imgurlLarge = imgurlLarge;
        this.imgURLSmall = imgURLSmall;
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

    public void setImgurlLarge(String imgurlLarge) {
        this.imgurlLarge = imgurlLarge;
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

    public String getImgurlLarge() {
        return imgurlLarge;
    }

    public String getImgURLSmall() {
        return imgURLSmall;
    }
}
