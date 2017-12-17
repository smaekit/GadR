package com.marcusjakobsson.gadr;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;

import com.google.android.gms.maps.CameraUpdate;
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
    public CameraUpdate cameraUpdate;


    public List<Bitmap> getIcon() {
        return icon;
    }

    public void setIcon(List<Bitmap> icon) {
        this.icon = icon;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //retain this fragment
        setRetainInstance(true);
    }
}
