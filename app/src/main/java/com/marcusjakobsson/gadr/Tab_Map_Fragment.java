package com.marcusjakobsson.gadr;

import android.content.Context;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by carlbratt on 2017-10-30.
 */

public class Tab_Map_Fragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    FirebaseConnection fc;




    LocationManager locationManager;
    LocationListener locationListener;


    private static final String TAG_RETAINED_MAP_FRAGMENT = "RetainedMapFragment";

    private RetainedMapFragment mData;

    GetBitmapFromURLAsync getBitmapFromURLAsync;
    Boolean isFragmentUp = false;
    Boolean cameFromEarlierView = false;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab_map, container, false);


        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately


        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        isFragmentUp = true;



        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mData.loc = new LatLng(location.getLatitude(), location.getLongitude());

                        locationManager.removeUpdates(locationListener);

                        FirebaseConnection firebaseConnection = new FirebaseConnection();
                        firebaseConnection.UpdateUserLocation(location.getLatitude(),location.getLongitude());

                        reloadUserData();


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };


        FragmentManager fm = getFragmentManager();
        mData = (RetainedMapFragment) fm.findFragmentByTag(TAG_RETAINED_MAP_FRAGMENT);

        // create the fragment and data the first time
        if (mData == null) {
            // add the fragment
            mData = new RetainedMapFragment();
            fm.beginTransaction().add(mData, TAG_RETAINED_MAP_FRAGMENT).commit();
            // load data from a data source or perform any calculation
            cameFromEarlierView = true;
            requestLocationUpdates();
        }else {
            reloadMapMarkers();

        }


        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                //reloadUserData();
                // find the retained fragment on activity restarts
                if(!cameFromEarlierView)
                {
                    reloadMapMarkers();
                    googleMap.moveCamera(mData.cameraUpdate);
                }

                // the data is available in mRetainedFragment.getData() even after
                // subsequent configuration change restarts.
            }
        });





    }

    public void updateCameraPosition(){
        if (googleMap != null && mData.loc != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder().target(mData.loc).zoom(17).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public void requestLocationUpdates()
    {
        // If device is running SDK < 23
        if (Build.VERSION.SDK_INT < 23)
        {
            //We can just request locationUpdates
            //we also need to target min sdk version less then 23
        }else
        {
            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //Ask for permisson
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }else
            {
                //If user already granted us permisson
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
                if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

                }
            }

        }
    }

    public void refreshUserLocationData()
    {
        if(isFragmentUp){
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //User location updates from here?
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            }
        }
    }


    public void reloadMapMarkers() {

        if (googleMap != null) {
            googleMap.clear();

            String today = new SimpleDateFormat(EventData.DATE_FORMAT_STRING).format(new Date());

            try{
                mData.allEventData = ((ThisApp) getActivity().getApplication()).getAllEvents();
                mData.myEventData = ((ThisApp) getActivity().getApplication()).getMyEvents();
            }catch (NullPointerException e)
            {
                Toast.makeText(getContext(), "could not retrive data", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }


            placeAllEventMarkers(today);

            placeMyEventMarkers(today);

            placeUserMarkes();
            //updateCameraPosition();
        }
    }

    private void placeAllEventMarkers(String today)
    {
        if (mData.allEventData != null) {
            for (int i = 0; i < mData.allEventData.length; i++) {
                if (mData.allEventData[i].getDate().equals(today))
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(mData.allEventData[i].getCustomLocation().getLatitude(), mData.allEventData[i].getCustomLocation().getLongitude()))
                            .title(mData.allEventData[i].getTitle())
                            .snippet(mData.allEventData[i].getStartTime() + " - " + mData.allEventData[i].getEndTime())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.beer)));
            }
        }
    }

    private void placeMyEventMarkers(String today)
    {
        if (mData.myEventData != null) {
            for (int i = 0; i < mData.myEventData.length; i++) {
                if (mData.myEventData[i].getDate().equals(today))
                    googleMap.addMarker(new MarkerOptions().position(new LatLng(mData.myEventData[i].getCustomLocation().getLatitude(), mData.myEventData[i].getCustomLocation().getLongitude())).title(mData.myEventData[i].getTitle()).snippet(mData.myEventData[i].getStartTime() + " - " + mData.myEventData[i].getEndTime()).icon(BitmapDescriptorFactory.fromResource(R.drawable.beer)));
            }
        }
    }

    private void placeUserMarkes()
    {
        if (mData.userData != null) {
            //for (UserData user : userData) {
            for (int i = 0; i < mData.userData.size(); i++) {
                if (mData.userData.get(i).getShareLocation()) {

                    LatLng latLng = new LatLng(mData.userData.get(i).getLatitude(), mData.userData.get(i).getLongitude());
                    if (mData.icon != null && mData.icon.size() > i && isFragmentUp) {
                        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(convertToRoundDrawable(mData.icon.get(i)));
                        googleMap.addMarker(new MarkerOptions().position(latLng).title(mData.userData.get(i).getName()).snippet(mData.userData.get(i).getStatus()).icon(markerIcon)).showInfoWindow();
                    } else {
                        googleMap.addMarker(new MarkerOptions().position(latLng).title(mData.userData.get(i).getName()).snippet(mData.userData.get(i).getStatus()).icon(BitmapDescriptorFactory.fromResource(R.drawable.person2))).showInfoWindow();
                    }
                }
            }
        }
    }

    private Drawable convertToRoundDrawable(Bitmap bitmap)
    {

            RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
            drawable.setCircular(true);
            mData.roundIcon.add(drawable);
            Drawable circleDrawable = drawable;
            return circleDrawable;

    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public void reloadUserData() {
        FirebaseConnection firebaseConnection = new FirebaseConnection();


        FirebaseConnection.UsersCallback usersCallback = new FirebaseConnection.UsersCallback() {
            @Override
            public void onSuccess(List<UserData> result) {
                mData.userData = result;

                if (mData.userData.size() > mData.icon.size()) {
                    mData.icon.clear();
                    mData.roundIcon.clear();
                    for (UserData user : result) {

                        getBitmapFromURLAsync = new GetBitmapFromURLAsync();
                        getBitmapFromURLAsync.execute(user.getImgURLSmall());
                    }
                }
                else {
                    reloadMapMarkers();
                    updateCameraPosition();
                }

            }
        };
        firebaseConnection.getUsers(usersCallback);
    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {

        if(getBitmapFromURLAsync != null){
            getBitmapFromURLAsync.cancel(true);
        }

        mData.cameraUpdate = CameraUpdateFactory.newLatLngZoom(googleMap.getCameraPosition().target,googleMap.getCameraPosition().zoom);
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        isFragmentUp = false;
        if (getBitmapFromURLAsync != null) {
            getBitmapFromURLAsync.cancel(true);
        }
        mMapView.onDestroy();

        //Should be destroyed last.
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }



    public static Bitmap getBitmapFromURL(String imgUrl) {
        try {
            URL url = new URL(imgUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    /**     AsyncTAsk for Image Bitmap  */
    private class GetBitmapFromURLAsync extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            return getBitmapFromURL(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

                mData.icon.add(bitmap);

                if (mData.userData.size() == mData.icon.size()) {

                    reloadMapMarkers();
                    updateCameraPosition();

                }

        }
    }
}
