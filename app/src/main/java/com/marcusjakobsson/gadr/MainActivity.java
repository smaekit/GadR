package com.marcusjakobsson.gadr;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    LoginButton loginButton;
    CallbackManager callbackManager;
    private FirebaseAuth mAuth;
    private static final String TAG = "FacebookLogin";
    ImageView gadrLogo;
    FirebaseConnection fc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gadrLogo = (ImageView)findViewById(R.id.userProfilePicture);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        fc = new FirebaseConnection();

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile", "user_friends");
        callbackManager = CallbackManager.Factory.create();


        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.i("FB:", "Callback success!");
                Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                Log.i("FB", loginResult.getAccessToken().getToken());
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // App code
                Log.i("FB:", "Callback cancel!");
                Toast.makeText(MainActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                updateUI(null);

            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.i("FB:", "Callback ERROR!" + exception.toString());
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                updateUI(null);

            }
        });

        /*
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = database.getReference();

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });*/
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    // [START auth_with_facebook]
    private void handleFacebookAccessToken(AccessToken token)
    {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null)
                            {
                                //This func checks if user exists in database if it does no update is made
                                //if not in database gets userInfo from FB and creates a new user
                                fc.checkIfUserAlreadyExists(user, new FirebaseConnection.CurrentUserCallback() {
                                    @Override
                                    public void onSuccess(Boolean result) {
                                        if(result)
                                        {
                                            updateUI(user);
                                        }
                                        else
                                        {
                                            getFacebookInfoFromUser(new MyCallbackListener()
                                            {
                                                @Override
                                                public void callback(UserData userData) {

                                                    userData.setFbID(user.getUid());
                                                    fc.AddUser(userData);   //Adding a user to the Firebase Database
                                                }
                                            });
                                            updateUI(user);
                                        }
                                    }
                                });
                            }




                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }
    // [END auth_with_facebook]


    public void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();

        updateUI(null);
    }

    //Here we send the user to next activity
    private void updateUI(FirebaseUser user) {
        //hideProgressDialog();
        if (user != null) {

            Intent intent = new Intent(this, MenuTabbedView.class);
            startActivity(intent);
            //user.getUid()));
            //loginButton.setVisibility(View.GONE);
            //findViewById(R.id.button_facebook_signout).setVisibility(View.VISIBLE);
        }
    }


    //Start get Facebook user info
    public void getFacebookInfoFromUser(final MyCallbackListener myCallbackListener)
    {
        final UserData user = new UserData();
        Bundle params = new Bundle();
        Bundle params2 = new Bundle();
        params.putString("fields", "name,picture.type(large)");    //Params what you want to get from the FB-user
        params2.putString("fields", "picture");                      //Graph request for the small profilepicture

        //Start GraphRequestBatch
        GraphRequestBatch batch = new GraphRequestBatch(

                //Start First GraphRequest
           new GraphRequest(AccessToken.getCurrentAccessToken(), "me", params, HttpMethod.GET, new GraphRequest.Callback()
           {
                @Override
                public void onCompleted(GraphResponse response)
                {
                    if (response != null)
                    {
                        try {

                            JSONObject data = response.getJSONObject();
                            Log.i("response", response.toString());
                            String profilePicUrl = "";
                            String name = "";

                            profilePicUrl = data.getJSONObject("picture").getJSONObject("data").getString("url");
                            Log.i("LargeUserPictureURl", profilePicUrl);
                            user.setImgURLLarge(profilePicUrl);

                            name = data.getString("name");
                            Log.i("Name", name);
                            user.setName(name);



                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }), //End First GraphRequest
            //Start Second GraphRequest
            new GraphRequest(AccessToken.getCurrentAccessToken(), "me", params2, HttpMethod.GET, new GraphRequest.Callback()
            {
                        @Override
                        public void onCompleted(GraphResponse response)
                        {
                            if (response != null)
                            {
                                try {

                                    JSONObject data = response.getJSONObject();
                                    Log.i("response", response.toString());

                                    String smallProfilePicUrl = data.getJSONObject("picture").getJSONObject("data").getString("url");
                                    Log.i("SmallUserPictureURl", smallProfilePicUrl);
                                    user.setImgURLSmall(smallProfilePicUrl);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
            }) //End Second GraphRequest

        ); //End GraphRequestBatch

        batch.addCallback(new GraphRequestBatch.Callback()
        {
            @Override
            public void onBatchCompleted(GraphRequestBatch batch) {

                myCallbackListener.callback(user);

            }
        });

        batch.executeAsync();

    }  //End get Facebook user info

}




