package com.marcusjakobsson.gadr;

import android.app.Application;

/**
 * Created by carlbratt on 2017-11-13.
 */

/*
        // set
        ((ThisApp) getActivity().getApplication()).setTestString("foo");

        // get
        String s = ((ThisApp) getActivity().getApplication()).getTestString();
 */

public class ThisApp extends Application {

    private static final String TAG = "ThisApp";

    private EventData[] allEvents;
    private EventData[] myEvents;

    public EventData[] getAllEvents() {
        return allEvents;
    }

    public EventData[] getMyEvents() {
        return myEvents;
    }


    public void setAllEvents(EventData[] allEvents) {
        this.allEvents = allEvents;
    }

    public void setMyEvents(EventData[] myEvents) {
        this.myEvents = myEvents;
    }



}
