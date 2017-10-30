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

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;


    public FirebaseConnection() {
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference();

        Log.w(TAG, mDatabaseRef.child("posts").push().getKey());

        //mDatabaseRef.child("Users").child("Id123").child("username").setValue("uservalue");
    }

/*    public void AddEvent(String userID, String eventName){

    }*/


/*    public void SetFirebaseValue(String string){
        if (mDatabaseRef != null){
            mDatabaseRef.setValue(string);
        }
    }*/




}


