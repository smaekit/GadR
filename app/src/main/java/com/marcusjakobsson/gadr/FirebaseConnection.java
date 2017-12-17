package com.marcusjakobsson.gadr;

import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
 * Contains database functions.
 */

class FirebaseConnection {
    private static final String TAG = "FirebaseConnection";

    //Constants

    //Users
    public static final String users_parent = "Users";
    public static String users_name = "name";
    public static String users_imgURLLarge = "imgurlLarge";
    public static String users_imgURLSmall = "imgurlLarge";
    public static final String users_status = "status";
    public static final String users_latitude = "latitude";
    public static final String users_longitude = "longitude";
    public static final String users_shareLocation = "shareLocation";

    //Event
    public static final String event_parent = "Events2";

    //Interfaces
    public interface GetUsersCallback {
        void onSuccess(List<UserData> result);
        void onFail(String string);
    }

    public interface CurrentUserCallback{
        void onSuccess(Boolean result);
        void onFail(String string);
    }

    public interface GetEventsCallback {
        void onSuccess(List<EventData> result,List<String> keys);
        void onFail(String error);
    }

    public interface GetDataCallback {
        void onSuccess();
        void onFail(String error);
    }

    //Variables
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;


    public FirebaseConnection() {
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
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
                callback.onFail(databaseError.getMessage());
            }
        };
        ref.addListenerForSingleValueEvent(valueEventListener);
    }

    public void getUsers(final GetUsersCallback callback) {

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
                callback.onFail(databaseError.getMessage());
            }
        };
        ref.addListenerForSingleValueEvent(valueEventListener);
    }

    public void getEvents(final GetEventsCallback callback) {
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
                callback.onFail(databaseError.getMessage());
            }
        };
        ref.addListenerForSingleValueEvent(valueEventListener);
    }

    public void UpdateStatus(final String status, final GetDataCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            String userID = currentUser.getUid();
            DatabaseReference ref = mRef.child(users_parent).child(userID).child(users_status);

            Task setValueTask = ref.setValue(status);

            setValueTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    callback.onFail(e.getMessage());
                }
            });

            setValueTask.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    callback.onSuccess();
                }
            });
        }
        else {
            callback.onFail(Resources.getSystem().getString(R.string.Error_NoUser));
        }
    }


    public void EditEvent(EventData eventData, String key, final GetDataCallback callback) {
        DatabaseReference ref = mRef.child(event_parent).child(key);

        Task setValueTask = ref.setValue(eventData);

        setValueTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFail(e.getMessage());
            }
        });

        setValueTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                callback.onSuccess();
            }
        });
    }

    public void AddEvent(EventData eventData, final GetDataCallback callback) {
        DatabaseReference ref = mRef.child(event_parent).push();

        Task setValueTask = ref.setValue(eventData);

        setValueTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFail(e.getMessage());
            }
        });

        setValueTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                callback.onSuccess();
            }
        });
    }

    public void AddUser(UserData userData, final GetDataCallback callback) {
        DatabaseReference ref = mRef.child(users_parent).child(userData.getFbID());
        Task setValueTask = ref.setValue(userData);

        setValueTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFail(e.getMessage());
            }
        });

        setValueTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                callback.onSuccess();
            }
        });
    }

    public void UpdateUserLocation(double latitude, final double longitude, final GetDataCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            final String userID = currentUser.getUid();
            DatabaseReference latRef = mRef.child(users_parent).child(userID).child(users_latitude);


            final Task setLat = latRef.setValue(latitude);

            setLat.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    callback.onFail(e.getMessage());
                }
            });

            setLat.addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    DatabaseReference lngRef = mRef.child(users_parent).child(userID).child(users_longitude);
                    Task setLng = lngRef.setValue(longitude);

                    setLng.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            callback.onFail(e.getMessage());
                        }
                    });

                    setLng.addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            callback.onSuccess();
                        }
                    });
                }
            });
        }
        else {
            callback.onFail(Resources.getSystem().getString(R.string.Error_NoUser));
        }
    }

    public void UpdateUserShareLocation(Boolean isSharingLocation, final GetDataCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            DatabaseReference ref = mRef.child(users_parent).child(currentUser.getUid()).child(users_shareLocation);
            Task setValueTask = ref.setValue(isSharingLocation);

            setValueTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    callback.onFail(e.getMessage());
                }
            });

            setValueTask.addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    callback.onSuccess();
                }
            });
        }
        else {
            callback.onFail(Resources.getSystem().getString(R.string.Error_NoUser));
        }
    }
}


