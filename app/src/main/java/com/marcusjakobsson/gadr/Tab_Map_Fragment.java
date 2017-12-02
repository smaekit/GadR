package com.marcusjakobsson.gadr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import java.util.List;

/**
 * Created by carlbratt on 2017-10-30.
 */

public class Tab_Map_Fragment extends Fragment{

    MapView mMapView;
    private GoogleMap googleMap;
    FirebaseConnection fc;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView  = inflater.inflate(R.layout.fragment_tab_map,container,false);

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

                FirebaseConnection firebaseConnection = new FirebaseConnection();

                FirebaseConnection.UsersCallback usersCallback = new FirebaseConnection.UsersCallback() {
                    @Override
                    public void onSuccess(List<UserData> result) {
                        LatLng jkpg = new LatLng(57.7824464, 14.176048900000069);
                        for (UserData user : result)
                        {
                            LatLng latLng = new LatLng(user.getLatitude(), user.getLongitude());
                            googleMap.addMarker(new MarkerOptions().position(latLng).title(user.getName()).snippet(user.getStatus()).icon(BitmapDescriptorFactory.fromResource(R.drawable.person2))).showInfoWindow();
                        }
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(jkpg).zoom(13).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                };
                firebaseConnection.getUsers(usersCallback);


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


        return rootView ;
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
}
