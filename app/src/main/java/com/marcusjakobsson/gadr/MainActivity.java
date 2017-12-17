package com.marcusjakobsson.gadr;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
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

public class MainActivity extends AppCompatActivity {

    private static final String FB_INFO_DPI_SMALL = "name,picture.type(normal)";
    private static final String FB_INFO_DPI_240 = "name,picture.width(125).height(125)";
    private static final String FB_INFO_DPI_320 = "name,picture.width(150).height(150)";
    private static final String FB_INFO_DPI_480 = "name,picture.width(225).height(225)";
    private static final String FB_INFO_DPI_LARGE = "name,picture.width(300).height(300)";
    private static final String FB_ICON_DPI_NORMAL = "picture.type(normal)";
    private static final String FB_REQUEST_FIELDS = "fields";


    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;
    private static final String TAG = "FacebookLogin";
    private ImageView gadrLogo;
    private FirebaseConnection fc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gadrLogo = findViewById(R.id.userProfilePicture);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        fc = new FirebaseConnection();

        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile", "user_friends");
        callbackManager = CallbackManager.Factory.create();


        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                handleFacebookAccessToken(loginResult.getAccessToken());

                loginButton.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancel() {
                // App code
                MySnackbarProvider.showSnackBar(getWindow().getDecorView().getRootView(),getString(R.string.cancelled));
                updateUI(null);

            }

            @Override
            public void onError(FacebookException exception) {
                MySnackbarProvider.showSnackBar(getWindow().getDecorView().getRootView(),getString(R.string.connectionError));
                updateUI(null);

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if(mAuth.getCurrentUser() != null){
            FirebaseUser currentUser = mAuth.getCurrentUser();
            updateUI(currentUser);
            loginButton.setVisibility(View.VISIBLE);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    // [START auth_with_facebook]
    private void handleFacebookAccessToken(AccessToken token)
    {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            // Sign in success, update UI with the signed-in user's information
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

                                                    // Adding a user to the Firebase Database
                                                    fc.AddUser(userData, new FirebaseConnection.GetDataCallback() {
                                                        @Override
                                                        public void onSuccess() {

                                                        }

                                                        @Override
                                                        public void onFail(String error) {
                                                            Toast.makeText(getApplicationContext(),error, Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                                }
                                            });
                                            updateUI(user);
                                        }
                                    }

                                    @Override
                                    public void onFail(String error) {
                                        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }




                        } else {
                            // If sign in fails, display a message to the user.
                            MySnackbarProvider.showSnackBar(getWindow().getDecorView().getRootView(),getString(R.string.authFailed));
                            updateUI(null);
                        }
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

        if (user != null) {

            Intent intent = new Intent(this, MenuTabbedView.class);
            startActivity(intent);
        }
    }


    //Get picture bundle depending on DPI of device
    private Bundle getPictureBundleDPI(Bundle params)
    {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        Integer dpi = metrics.densityDpi;
        if (dpi < 240){
            params.putString(FB_REQUEST_FIELDS, FB_INFO_DPI_SMALL);
        }else if(dpi == 240){
            params.putString(FB_REQUEST_FIELDS, FB_INFO_DPI_240);
        } else if(dpi == 320){
            params.putString(FB_REQUEST_FIELDS, FB_INFO_DPI_320);
        } else if(dpi == 480){
            params.putString(FB_REQUEST_FIELDS, FB_INFO_DPI_480);
        }else {
            params.putString(FB_REQUEST_FIELDS, FB_INFO_DPI_LARGE);
        }

        return params;
    }


    //Start get Facebook user info
    private void getFacebookInfoFromUser(final MyCallbackListener myCallbackListener)
    {
        final UserData user = new UserData();
        Bundle params = new Bundle();
        Bundle params2 = new Bundle();

        params = getPictureBundleDPI(params);   //Params what you want to get from the FB-user depending on DPI of device
        params2.putString(FB_REQUEST_FIELDS, FB_ICON_DPI_NORMAL);

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

                            String profilePicUrl;
                            String name;

                            profilePicUrl = data.getJSONObject("picture").getJSONObject("data").getString("url");
                            user.setImgURLLarge(profilePicUrl);

                            name = data.getString("name");
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

                                    String smallProfilePicUrl = data.getJSONObject("picture").getJSONObject("data").getString("url");
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




