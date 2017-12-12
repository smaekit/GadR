package com.marcusjakobsson.gadr;

/**
 * Created by carlbratt on 2017-11-08.
 */

public class CustomListViewItem {

    private String title;
    private String time;
    private String place;
    private int categoryIndex;


    public CustomListViewItem() {
        this.title = "";
        this.time = "";
        this.place = "";
        this.categoryIndex = 0;
    }

    public CustomListViewItem(String title, String time, String place, int categoryIndex) {
        this.title = title;
        this.time = time;
        this.place = place;
        this.categoryIndex = categoryIndex;
    }

    void setTitle(String title) {
        this.title = title;
    }

    void setTime(String time) {
        this.time = time;
    }

    void setPlace(String place) {
        this.place = place;
    }

    void setCategoryIndex(int categoryIndex) {
        this.categoryIndex = categoryIndex;
    }

    String getTitle() {
        return title;
    }

    String getTime() {
        return time;
    }

    String getPlace() {
        return place;
    }

    public int getCategoryIndex() {
        return categoryIndex;
    }
}
