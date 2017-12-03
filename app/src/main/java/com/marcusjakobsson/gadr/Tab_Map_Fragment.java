package com.marcusjakobsson.gadr;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by carlbratt on 2017-10-30.
 */

public class Tab_Map_Fragment extends Fragment{

    MapView mMapView;
    private GoogleMap googleMap;
    FirebaseConnection fc;
    private List<Bitmap> icon;
    private List<UserData> userData;
    LocationManager locationManager;
    LocationListener locationListener;
    LatLng loc;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView  = inflater.inflate(R.layout.fragment_tab_map,container,false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);



        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Ask for permisson
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i("users location", "HEF FITTOROOROROROOR");
                loc = new LatLng(location.getLatitude(), location.getLongitude());
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

        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }



        mMapView.onResume(); // needed to get the map to display immediately


        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        final FirebaseConnection firebaseConnection = new FirebaseConnection();

        FirebaseConnection.UsersCallback usersCallback = new FirebaseConnection.UsersCallback() {
            @Override
            public void onSuccess(List<UserData> result) {


                for (UserData user : result)
                {
                    if (user.getFbID() == firebaseConnection.getCurrentUserId() )
                    {
                        loc = new LatLng(user.getLatitude(),user.getLongitude());
                    }
                    userData.add(user);
//                            GetBitmapFromURLAsync getBitmapFromURLAsync = new GetBitmapFromURLAsync();
//                            getBitmapFromURLAsync.execute(user.getImgURLSmall());
                    //googleMap.addMarker(new MarkerOptions().position(latLng).title(user.getName()).snippet(user.getStatus()).icon(BitmapDescriptorFactory.fromResource(R.drawable.person2))).showInfoWindow();
                }
                mMapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap mMap) {
                        googleMap = mMap;

                        // For showing a move to my location button
                        //googleMap.setMyLocationEnabled(true);

                        for (UserData user : userData)
                        {
                            LatLng pos = new LatLng(user.getLatitude(), user.getLongitude());
                            googleMap.addMarker(new MarkerOptions().position(pos).title(user.getName()).snippet(user.getStatus()).icon(BitmapDescriptorFactory.fromResource(R.drawable.person2))).showInfoWindow();
                        }


                        LatLng jkpg = new LatLng(57.7824464, 14.176048900000069);
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(13).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));



                        // For dropping a marker at a point on the Map
//                LatLng jth = new LatLng(57.7779500801111, 14.161934852600098);
//                LatLng jkpg = new LatLng(57.7824464, 14.176048900000069);
//                googleMap.addMarker(new MarkerOptions().position(jkpg).title("Makkan").snippet("Kodar Android").icon(BitmapDescriptorFactory.fromResource(R.drawable.person2))).showInfoWindow();
//                googleMap.addMarker(new MarkerOptions().position(jth).title("Beerpong i JTH").snippet("19.00 - 21.00").icon(BitmapDescriptorFactory.fromResource(R.drawable.beer)));
//
//                // For zooming automatically to the location of the marker
//                CameraPosition cameraPosition = new CameraPosition.Builder().target(jkpg).zoom(13).build();
//                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                });

            }
        };

        firebaseConnection.getUsers(usersCallback);




        return rootView ;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
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
    private class GetBitmapFromURLAsync extends AsyncTask<String, Void, Bitmap>   {
        @Override
        protected Bitmap doInBackground(String... params) {
            return getBitmapFromURL(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            icon.add(bitmap);

        }
    }
}

