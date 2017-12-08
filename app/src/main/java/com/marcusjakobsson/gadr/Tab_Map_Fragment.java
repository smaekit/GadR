package com.marcusjakobsson.gadr;

import android.content.Context;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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

    EventData[] allEventData;
    EventData[] myEventData;
    List<UserData> userData;

    LocationManager locationManager;
    LocationListener locationListener;

    LatLng loc;
    List<RoundedBitmapDrawable> icon = new ArrayList<>();

    private static final String TAG_RETAINED_MAP_FRAGMENT = "RetainedMapFragment";

    private RetainedMapFragment mRetainedMapFragment;


    GetBitmapFromURLAsync getBitmapFromURLAsync;





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Init mapView
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

        // find the retained fragment on activity restarts
        FragmentManager fm = getFragmentManager();
        mRetainedMapFragment = (RetainedMapFragment) fm.findFragmentByTag(TAG_RETAINED_MAP_FRAGMENT);

        // create the fragment and data the first time
        if (mRetainedMapFragment == null) {
            // add the fragment
            mRetainedMapFragment = new RetainedMapFragment();
            fm.beginTransaction().add(mRetainedMapFragment, TAG_RETAINED_MAP_FRAGMENT).commit();
            // load data from a data source or perform any calculation

        }

        // the data is available in mRetainedFragment.getData() even after
        // subsequent configuration change restarts.


        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mRetainedMapFragment.loc = new LatLng(location.getLatitude(), location.getLongitude());
                locationManager.removeUpdates(locationListener);

                FirebaseConnection firebaseConnection = new FirebaseConnection();
                firebaseConnection.UpdateUserLocation(location.getLatitude(),location.getLongitude());


                    //reloadUserData();


                if (googleMap != null) {
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(mRetainedMapFragment.loc).zoom(17).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
                reloadEventMarkers();

                Toast.makeText(getContext(), "Map fragment refreshing updates", Toast.LENGTH_SHORT).show();

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


        // If device is running SDK < 23
        if (Build.VERSION.SDK_INT < 23)
        {
            //We can just request locationUpdates
            //we also need to target min sdk version less then 23
        }else
        {
            //Todo: make funciton
            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //Ask for permisson
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }else
            {
                //Check if we can retrieve state
                if(mRetainedMapFragment.loc == null)
                {
                    //If user already granted us permisson
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, locationListener);
                    }
                    if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 0, locationListener);
                    }
                }

            }

        }


        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                //googleMap.setMyLocationEnabled(true);


                if(mRetainedMapFragment.userData == null)
                {
                    //get from database
                    reloadUserData();
                }
                else
                {
                    //retrieve from saved state

                    reloadEventMarkers();
                }



            }
        });

    }

    public void reloadEventMarkers() {

        if (googleMap != null) {
            googleMap.clear();

            String today = new SimpleDateFormat(EventData.DATE_FORMAT_STRING).format(new Date());

            try{
                mRetainedMapFragment.allEventData = ((ThisApp) getActivity().getApplication()).getAllEvents();
                mRetainedMapFragment.myEventData = ((ThisApp) getActivity().getApplication()).getMyEvents();
            }catch (NullPointerException e)
            {
                e.printStackTrace();
            }


            if (mRetainedMapFragment.allEventData != null) {
                for (int i = 0; i < mRetainedMapFragment.allEventData.length; i++) {
                    if (mRetainedMapFragment.allEventData[i].getDate().equals(today))
                        googleMap.addMarker(new MarkerOptions().position(new LatLng(mRetainedMapFragment.allEventData[i].getCustomLocation().getLatitude(), mRetainedMapFragment.allEventData[i].getCustomLocation().getLongitude())).title(mRetainedMapFragment.allEventData[i].getTitle()).snippet(mRetainedMapFragment.allEventData[i].getStartTime() + " - " + mRetainedMapFragment.allEventData[i].getEndTime()).icon(BitmapDescriptorFactory.fromResource(R.drawable.beer)));
                }
            }

            if (mRetainedMapFragment.myEventData != null) {
                for (int i = 0; i < mRetainedMapFragment.myEventData.length; i++) {
                    if (mRetainedMapFragment.myEventData[i].getDate().equals(today))
                        googleMap.addMarker(new MarkerOptions().position(new LatLng(mRetainedMapFragment.myEventData[i].getCustomLocation().getLatitude(), mRetainedMapFragment.myEventData[i].getCustomLocation().getLongitude())).title(mRetainedMapFragment.myEventData[i].getTitle()).snippet(mRetainedMapFragment.myEventData[i].getStartTime() + " - " + mRetainedMapFragment.myEventData[i].getEndTime()).icon(BitmapDescriptorFactory.fromResource(R.drawable.beer)));
                }
            }

            if (mRetainedMapFragment.userData != null) {
                //for (UserData user : userData) {
                for (int i = 0; i < mRetainedMapFragment.userData.size(); i++) {
                    if (mRetainedMapFragment.userData.get(i).getShareLocation()) {
                        LatLng latLng = new LatLng(mRetainedMapFragment.userData.get(i).getLatitude(), mRetainedMapFragment.userData.get(i).getLongitude());
                        if (mRetainedMapFragment.icon != null && mRetainedMapFragment.icon.size() > 0) {
                            Drawable circleDrawable = mRetainedMapFragment.icon.get(i);
                            BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
                            googleMap.addMarker(new MarkerOptions().position(latLng).title(mRetainedMapFragment.userData.get(i).getName()).snippet(mRetainedMapFragment.userData.get(i).getStatus()).icon(markerIcon)).showInfoWindow();
                        } else {
                            googleMap.addMarker(new MarkerOptions().position(latLng).title(mRetainedMapFragment.userData.get(i).getName()).snippet(mRetainedMapFragment.userData.get(i).getStatus()).icon(BitmapDescriptorFactory.fromResource(R.drawable.person2))).showInfoWindow();
                        }
                    }
                }
            }
        }
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
                if (mRetainedMapFragment != null)
                mRetainedMapFragment.userData = result;
/*
                if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //Ask for permisson
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }

                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, locationListener);
                }
                if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 0, locationListener);
                }*/

                for (UserData user : result) {
                //    LatLng latLng = new LatLng(user.getLatitude(), user.getLongitude());
                //    googleMap.addMarker(new MarkerOptions().position(latLng).title(user.getName()).snippet(user.getStatus()).icon(BitmapDescriptorFactory.fromResource(R.drawable.person2))).showInfoWindow();
                    getBitmapFromURLAsync = new GetBitmapFromURLAsync();
                    getBitmapFromURLAsync.execute(user.getImgURLSmall());
                }
/*                if (loc != null){
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(17).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }*/

                reloadEventMarkers();
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
    public void onStop() {
        // notice here that I keep a reference to the task being executed as a class member:
        if (this.getBitmapFromURLAsync != null && this.getBitmapFromURLAsync.getStatus() == AsyncTask.Status.RUNNING) this.getBitmapFromURLAsync.cancel(true);
        super.onStop();
    }

    @Override
    public void onPause() {
        // perform other onPause related actions
        if (getBitmapFromURLAsync != null) {
            getBitmapFromURLAsync.cancel(true);
        }
        super.onPause();
        mMapView.onPause();
        // this means that this activity will not be recreated now, user is leaving it
        // or the activity is otherwise finishing
        if(isRemoving()) {
            FragmentManager fm = getFragmentManager();
            // we will not need this fragment anymore, this may also be a good place to signal
            // to the retained fragment object to perform its own cleanup.
            fm.beginTransaction().remove(mRetainedMapFragment).commit();
        }
    }

    @Override
    public void onDestroy() {
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
            RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create( getResources(),bitmap);
            drawable.setCircular(true);
            mRetainedMapFragment.icon.add(drawable);

            if (mRetainedMapFragment.userData.size() == mRetainedMapFragment.icon.size())
            {
                reloadEventMarkers();
            }
        }
    }
}
