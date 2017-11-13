package com.marcusjakobsson.gadr;

import android.util.Log;

import com.facebook.CallbackManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

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
    public static String users_imgURL_large = "imageURL_large";
    public static String users_imgURL_small = "imageURL_small";


    //Event
    public static String event_parent = "Event";


    //Status
    public static String status_parent = "Status";


    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;


    public FirebaseConnection() {
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();
    }



    public void AddEvent(EventData eventData) {
        DatabaseReference ref = mRef.child(event_parent).push();

        ref.setValue(eventData);
    }

    public void AddUser(UserData userData) {
        DatabaseReference ref = mRef.child(users_parent).child(userData.getFbID());

        ref.child(users_name).setValue(userData.getName());
        ref.child(users_points).setValue(userData.getPoints());
        ref.child(users_imgURL_large).setValue(userData.getImgURL_large());
        ref.child(users_imgURL_small).setValue(userData.getImgURL_small());
    }

    public void AddStatus(StatusData statusData) {
        DatabaseReference ref = mRef.child(status_parent).push();

        ref.setValue(statusData);
    }
}


