package com.marcusjakobsson.gadr;

import com.google.android.gms.maps.model.Marker;

import java.util.List;

/**
 * Created by carlbratt on 2017-12-14.
 * Markers with and index corresponding to their place in the corresponding arrays/lists.
 */

class EventMarker {

    private Marker marker;
    private int index;

    public EventMarker(Marker marker, int index) {
        this.marker = marker;
        this.index = index;
    }

    private Marker getMarker() {
        return marker;
    }

    public int getIndex() {
        return index;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public void setIndex(int index) {
        this.index = index;
    }


    public static int ContainsMarker(List<EventMarker> eventMarkerList, Marker marker) {
        for (int i = 0; i < eventMarkerList.size(); i++) {
            if (eventMarkerList.get(i).getMarker().equals(marker)){
                return i;
            }
        }
        return -1;
    }
}
