package com.marcusjakobsson.gadr;

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
    List<Bitmap> icon = new ArrayList<>();



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

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                //googleMap.setMyLocationEnabled(true);

                reloadUserData();




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


        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                loc = new LatLng(location.getLatitude(), location.getLongitude());
                locationManager.removeUpdates(locationListener);
                FirebaseConnection firebaseConnection = new FirebaseConnection();
                firebaseConnection.UpdateUserLocation(location.getLatitude(),location.getLongitude());

                if (googleMap != null) {
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(17).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
                reloadEventMarkers();

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



        return rootView;
    }

    public void reloadEventMarkers() {

        if (googleMap != null) {
            googleMap.clear();

            String today = new SimpleDateFormat(EventData.DATE_FORMAT_STRING).format(new Date());

            allEventData = ((ThisApp) getActivity().getApplication()).getAllEvents();
            myEventData = ((ThisApp) getActivity().getApplication()).getMyEvents();

            if (allEventData != null) {
                for (int i = 0; i < allEventData.length; i++) {
                    if (allEventData[i].getDate().equals(today))
                        googleMap.addMarker(new MarkerOptions().position(new LatLng(allEventData[i].getCustomLocation().getLatitude(), allEventData[i].getCustomLocation().getLongitude())).title(allEventData[i].getTitle()).snippet(allEventData[i].getStartTime() + " - " + allEventData[i].getEndTime()).icon(BitmapDescriptorFactory.fromResource(R.drawable.beer)));
                }
            }

            if (myEventData != null) {
                for (int i = 0; i < myEventData.length; i++) {
                    if (myEventData[i].getDate().equals(today))
                        googleMap.addMarker(new MarkerOptions().position(new LatLng(myEventData[i].getCustomLocation().getLatitude(), myEventData[i].getCustomLocation().getLongitude())).title(myEventData[i].getTitle()).snippet(myEventData[i].getStartTime() + " - " + myEventData[i].getEndTime()).icon(BitmapDescriptorFactory.fromResource(R.drawable.beer)));
                }
            }

            if (userData != null) {
                //for (UserData user : userData) {
                for (int i = 0; i < userData.size(); i++) {
                    LatLng latLng = new LatLng(userData.get(i).getLatitude(), userData.get(i).getLongitude());
                    if (icon != null && icon.size() > 0) {
                        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(),icon.get(i));
                        drawable.setCircular(true);
                        googleMap.addMarker(new MarkerOptions().position(latLng).title(userData.get(i).getName()).snippet(userData.get(i).getStatus()).icon(BitmapDescriptorFactory.fromBitmap(icon.get(i)))).showInfoWindow();
                    }
                    else {
                        googleMap.addMarker(new MarkerOptions().position(latLng).title(userData.get(i).getName()).snippet(userData.get(i).getStatus()).icon(BitmapDescriptorFactory.fromResource(R.drawable.person2))).showInfoWindow();
                    }
                }
            }


            //TODO: remove
            LatLng jth = new LatLng(57.7779500801111, 14.161934852600098);
            LatLng jkpg = new LatLng(57.7824464, 14.176048900000069);
            googleMap.addMarker(new MarkerOptions().position(jkpg).title("Makkan").snippet("Kodar Android").icon(BitmapDescriptorFactory.fromResource(R.drawable.person2))).showInfoWindow();
            googleMap.addMarker(new MarkerOptions().position(jth).title("Beerpong i JTH").snippet("19.00 - 21.00").icon(BitmapDescriptorFactory.fromResource(R.drawable.beer)));
        }
    }


    public void reloadUserData() {
        FirebaseConnection firebaseConnection = new FirebaseConnection();


        FirebaseConnection.UsersCallback usersCallback = new FirebaseConnection.UsersCallback() {
            @Override
            public void onSuccess(List<UserData> result) {
                userData = result;

                for (UserData user : result) {
                //    LatLng latLng = new LatLng(user.getLatitude(), user.getLongitude());
                //    googleMap.addMarker(new MarkerOptions().position(latLng).title(user.getName()).snippet(user.getStatus()).icon(BitmapDescriptorFactory.fromResource(R.drawable.person2))).showInfoWindow();
                    GetBitmapFromURLAsync getBitmapFromURLAsync = new GetBitmapFromURLAsync();
                    getBitmapFromURLAsync.execute(user.getImgURLSmall());
                }
                if (loc != null){
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(17).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }

                if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //Ask for permisson
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
                else
                {
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, locationListener);
                    }
                    if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 0, locationListener);
                    }
                }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 0, locationListener);


        }
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
            icon.add(bitmap);

            if (userData.size() == icon.size())
            {
                reloadEventMarkers();
            }
        }
    }}
