package com.marcusjakobsson.gadr;

/**
 * Created by carlbratt on 2017-11-08.
 */

public class CustomListViewItem {

    private String title;
    private String time;


    public CustomListViewItem() {
        this.title = "Default"; //TODO
        this.time = "00:00 - 01:00"; //TODO
    }

    public CustomListViewItem(String title, String time) {
        this.title = title;
        this.time = time;
    }

    void setTitle(String title) {
        this.title = title;
    }

    void setTime(String time) {
        this.time = time;
    }

    String getTitle() {
        return title;
    }

    String getTime() {
        return time;
    }
}
