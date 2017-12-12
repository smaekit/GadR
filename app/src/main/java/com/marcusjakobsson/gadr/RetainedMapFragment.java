package com.marcusjakobsson.gadr;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mäki on 2017-12-08.
 */

public class RetainedMapFragment extends Fragment {
    public EventData[] allEventData;
    public EventData[] myEventData;
    public List<UserData> userData = new ArrayList<>();
    public LatLng loc;
    public List<Bitmap> icon = new ArrayList<>();
    public List<RoundedBitmapDrawable> roundIcon = new ArrayList<>();

    public EventData[] getAllEventData() {
        return allEventData;
    }

    public void setAllEventData(EventData[] allEventData) {
        this.allEventData = allEventData;
    }

    public EventData[] getMyEventData() {
        return myEventData;
    }

    public void setMyEventData(EventData[] myEventData) {
        this.myEventData = myEventData;
    }

    public List<UserData> getUserData() {
        return userData;
    }

    public void setUserData(List<UserData> userData) {
        this.userData = userData;
    }

    public LatLng getLoc() {
        return loc;
    }

    public void setLoc(LatLng loc) {
        this.loc = loc;
    }

    public List<Bitmap> getIcon() {
        return icon;
    }

    public void setIcon(List<Bitmap> icon) {
        this.icon = icon;
    }

    public List<RoundedBitmapDrawable> getRoundIcon() {
        return roundIcon;
    }

    public void setRoundIcon(List<RoundedBitmapDrawable> roundIcon) {
        this.roundIcon = roundIcon;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        //retain this fragment
        setRetainInstance(true);
    }
}
