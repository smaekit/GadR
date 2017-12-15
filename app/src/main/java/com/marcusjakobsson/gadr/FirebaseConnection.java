package com.marcusjakobsson.gadr;

import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlbratt on 2017-10-30.
 */

public class FirebaseConnection {
    private static final String TAG = "FirebaseConnection";

    //Constants

    //Users
    public static String users_parent = "Users";
    public static String users_name = "name";
    public static String users_imgURLLarge = "imgurlLarge";
    public static String users_imgURLSmall = "imgurlLarge";
    public static String users_status = "status";
    public static String users_latitude = "latitude";
    public static String users_longitude = "longitude";
    public static String users_shareLocation = "shareLocation";


    //Event                             //TODO: change back to Events
    public static String event_parent = "Events2";

    public interface UsersCallback{
        void onSuccess(List<UserData> result);
    }

    public interface CurrentUserCallback{
        void onSuccess(Boolean result);
    }

    public interface EventsCallback{
        void onSuccess(List<EventData> result,List<String> keys);
    }

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;


    public FirebaseConnection() {
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    public void checkIfUserAlreadyExists(final FirebaseUser user, final CurrentUserCallback callback) {
        DatabaseReference ref = mRef.child(users_parent);

        final List<UserData> userData = new ArrayList<>();

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    userData.add(ds.getValue(UserData.class));
                }

                for (int i = 0; i < userData.size(); i++) {

                    if (userData.get(i).getFbID().equals(user.getUid()) )
                    {
                        callback.onSuccess(true);
                        return;
                    }
                }
                callback.onSuccess(false);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO: Handle error
            }
        };
        ref.addListenerForSingleValueEvent(valueEventListener);
    }

//Users callback func
    public void getUsers(final UsersCallback callback) {

        DatabaseReference ref = mRef.child(users_parent);

        final List<UserData> userData = new ArrayList<>();

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    userData.add(ds.getValue(UserData.class));
                }
                callback.onSuccess(userData);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO: Handle error
            }
        };
        ref.addListenerForSingleValueEvent(valueEventListener);
    }

//Event callback func
    public void getEvents(final EventsCallback callback) {
        DatabaseReference ref = mRef.child(event_parent);

        final List<EventData> eventData = new ArrayList<>();
        final List<String> keys = new ArrayList<>();

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    eventData.add(ds.getValue(EventData.class));
                    keys.add(ds.getKey());
                }
                callback.onSuccess(eventData,keys);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO: Handle error
            }
        };
        ref.addListenerForSingleValueEvent(valueEventListener);
    }


    public void EditEvent(EventData eventData, String key) {
        DatabaseReference ref = mRef.child(event_parent).child(key);

        ref.setValue(eventData);
    }

    public void AddEvent(EventData eventData) {
        DatabaseReference ref = mRef.child(event_parent).push();

        ref.setValue(eventData);
    }

    public void AddUser(UserData userData) {
        DatabaseReference ref = mRef.child(users_parent).child(userData.getFbID());
        ref.setValue(userData);
    }


    public void AddStatus(String status) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        DatabaseReference ref = mRef.child(users_parent).child(currentUser.getUid()).child(users_status);
        ref.setValue(status);
    }


    public void UpdateUserLocation(double latitude, double longitude)
    {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        DatabaseReference ref = mRef.child(users_parent).child(currentUser.getUid()).child(users_latitude);
        ref.setValue(latitude);
        ref = mRef.child(users_parent).child(currentUser.getUid()).child(users_longitude);
        ref.setValue(longitude);
    }

    public void UpdateUserShareLocation(Boolean isSharingLocation)
    {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        DatabaseReference ref = mRef.child(users_parent).child(currentUser.getUid()).child(users_shareLocation);
        ref.setValue(isSharingLocation);
    }

}


