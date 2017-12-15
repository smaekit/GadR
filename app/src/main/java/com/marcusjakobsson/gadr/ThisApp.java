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

class ThisApp extends Application {

    private static final String TAG = "ThisApp";

    private EventData[] allEvents;
    private EventData[] myEvents;

    //Keys
    private String[] allEventsKeys;
    private String[] myEventsKeys;

    //Getters
    public EventData[] getAllEvents() {
        return allEvents;
    }

    public EventData getAllEventByIndex(int position) {
        return allEvents[position];
    }

    public String getAllEventKeyByIndex(int position) { return allEventsKeys[position]; }

    public EventData[] getMyEvents() {
        return myEvents;
    }

    public EventData getMyEventByIndex(int position) { return myEvents[position]; }

    public String getMyEventKeyByIndex(int position) { return myEventsKeys[position]; }

    //Setters
    public void setAllEvents(EventData[] allEvents) {
        this.allEvents = allEvents;
    }

    public void setMyEvents(EventData[] myEvents) {
        this.myEvents = myEvents;
    }

    public void setAllEventsKeys(String[] allEventsKeys) { this.allEventsKeys = allEventsKeys; }

    public void setMyEventsKeys(String[] myEventsKeys) { this.myEventsKeys = myEventsKeys; }
}
