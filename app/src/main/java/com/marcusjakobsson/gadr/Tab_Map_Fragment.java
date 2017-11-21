package com.marcusjakobsson.gadr;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by carlbratt on 2017-10-30.
 */

public class Tab_Map_Fragment extends Fragment{

    MapView mMapView;
    private GoogleMap googleMap;
    View rootView;
    public static final int DEFAULT_ZOOM = 13;

    //widgets
    private EditText searchText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView  = inflater.inflate(R.layout.fragment_tab_map,container,false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        searchText = (EditText)rootView.findViewById(R.id.input_search);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);



        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                //googleMap.setMyLocationEnabled(true);

                // For dropping a marker at a point on the Map
                LatLng jth = new LatLng(57.7779500801111, 14.161934852600098);
                LatLng jkpg = new LatLng(57.7824464, 14.176048900000069);
                googleMap.addMarker(new MarkerOptions().position(jkpg).title("Makkan").snippet("Kodar Android").icon(BitmapDescriptorFactory.fromResource(R.drawable.person2))).showInfoWindow();
                googleMap.addMarker(new MarkerOptions().position(jth).title("Beerpong i JTH").snippet("19.00 - 21.00").icon(BitmapDescriptorFactory.fromResource(R.drawable.beer)));

                // For zooming automatically to the location of the marker
                moveCamera(jkpg,DEFAULT_ZOOM);
            }
        });


        return rootView ;
    }

    private void init(){
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_SEARCH
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){
                    //Execute method for searching
                    geoLocate();

                }
                return false;
            }
        });
        hideSoftKeyboard();

    }

    //Func to convert an address to a latlng
    private void geoLocate() {
        Log.i("geoLocate", "geolocating");
        String searchString = searchText.getText().toString();
        Geocoder geocoder = new Geocoder(getContext());
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString,1);
        }catch (IOException e) {
            Log.e("Geolocate", e.getMessage());
        }

        if(list.size() > 0){
            Address address = list.get(0);
            Log.i("geoLocate", address.toString());
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()),DEFAULT_ZOOM);

        }
    }

    //Func to move the camera of the map on the given latlng with the given zoom
    private void moveCamera(LatLng latLng, float zoom){
        try{
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(zoom).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            hideSoftKeyboard();
        }catch (NullPointerException e){
            Log.e("moveCamera", "onComplete: NullPointerException: " +e.getMessage() );
        }

    }

    private void hideSoftKeyboard(){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        try{
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }catch (NullPointerException e)
        {
            Log.e("hideSoftKeyboard", "onComplete: NullPointerException: " +e.getMessage() );
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
}
