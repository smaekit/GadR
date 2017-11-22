package com.marcusjakobsson.gadr;

import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

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
    public static String users_points = "points";
    public static String users_imgURLLarge = "imgurlLarge";
    public static String users_imgURLSmall = "imgurlLarge";


    //Event
    public static String event_parent = "Events";


    //Status
    public static String status_parent = "Status";



    public interface UsersCallback{
        void onSuccess(List<UserData> result);
    }

    public interface CurrentUserCallback{
        void onSuccess(Boolean result);
    }

    public interface EventsCallback{
        void onSuccess(List<EventData> result);
    }

    public interface StatusCallback{
        void onSuccess(List<StatusData> result);
    }



    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;


    public FirebaseConnection() {
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();
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
                        Log.i(TAG,"User found in database");
                        callback.onSuccess(true);
                        return;
                    }
                }
                Log.i(TAG,"No user was found in database");
                callback.onSuccess(false);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUsers:onCancelled", databaseError.toException());
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
                Log.w(TAG, "getUsers:onCancelled", databaseError.toException());
            }
        };
        ref.addListenerForSingleValueEvent(valueEventListener);


        //Call with:
        /*
        FirebaseConnection fc = new FirebaseConnection();

        fc.getUsers(new FirebaseConnection.UsersCallback(){
            @Override
            public void onSuccess(String result){
                //Do somthing with result
            }
        });
         */
    }

//Event callback func
    public void getEvents(final EventsCallback callback) {
        DatabaseReference ref = mRef.child(event_parent);

        final List<EventData> eventData = new ArrayList<>();

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    eventData.add(ds.getValue(EventData.class));
                }
                callback.onSuccess(eventData);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getEvents:onCancelled", databaseError.toException());
            }
        };
        ref.addListenerForSingleValueEvent(valueEventListener);
    }


// Status callback func
    public void getStatus(final StatusCallback callback) {
        DatabaseReference ref = mRef.child(status_parent);

        final List<StatusData> statusData = new ArrayList<>();

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    statusData.add(ds.getValue(StatusData.class));
                }
                callback.onSuccess(statusData);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getStatus:onCancelled", databaseError.toException());
            }
        };
        ref.addListenerForSingleValueEvent(valueEventListener);
    }



    public void AddEvent(EventData eventData) {
        DatabaseReference ref = mRef.child(event_parent).push();

        ref.setValue(eventData);
    }

    public void AddUser(UserData userData) {
        DatabaseReference ref = mRef.child(users_parent).child(userData.getFbID());
        ref.setValue(userData);
    }


    public void AddStatus(StatusData statusData) {
        DatabaseReference ref = mRef.child(status_parent).push();

        ref.setValue(statusData);
    }



}


