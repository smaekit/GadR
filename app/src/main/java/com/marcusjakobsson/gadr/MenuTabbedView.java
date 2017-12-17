package com.marcusjakobsson.gadr;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;

import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;

import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;




import java.io.IOException;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;

import java.util.List;


public class MenuTabbedView extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MenuTabbedView";
    private static final String MESSAGE_TAG = "msg";
    private static final String MESSAGE_BODY_TAG = "msgBody";

    private SectionsPagerAdapter sectionsPageAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private GetBitmapFromURLAsync getBitmapFromURLAsync;
    private static final String TAG_RETAINED_MENU_FRAGMENT = "RetainedMenuTabbedFragment";
    private RetainedMenuTabbedFragmet mData;

    private TextView userStatus_TextView;

    //Fragments
    private Tab_Map_Fragment tabMapFragment;
    private Tab_All_Events_Fragment tabAllEventsFragment;
    private Tab_My_Events_Fragment tabMyEventsFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed_view);

        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int code = api.isGooglePlayServicesAvailable(this);
        if (code == ConnectionResult.SUCCESS) {
            // We got the functionality let go!
        } else {
            AlertDialog alertDialog =
                    new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert).setMessage(
                            R.string.play_services_message)
                            .create();
            alertDialog.show();
        }

        sectionsPageAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        tabMapFragment = (Tab_Map_Fragment) getFragment(0);
        if(tabMapFragment == null){
            tabMapFragment = new Tab_Map_Fragment();
        }

        tabAllEventsFragment = (Tab_All_Events_Fragment) getFragment(1);
        if (tabAllEventsFragment == null) {
            tabAllEventsFragment = new Tab_All_Events_Fragment();
        }

        tabMyEventsFragment = (Tab_My_Events_Fragment) getFragment(2);
        if (tabMyEventsFragment == null) {
            tabMyEventsFragment = new Tab_My_Events_Fragment();
        }

        viewPager = findViewById(R.id.container);
        viewPager.setOffscreenPageLimit(3);  //How many screens before reload
        tabLayout = findViewById(R.id.tabs);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        // If device is running SDK < 23
        if (Build.VERSION.SDK_INT < 23)
        {
            //We can just request locationUpdates
        }else
        {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //Ask for permission
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            else
            {
                //We have permission
            }
        }

        Toolbar toolbar = findViewById(R.id.top_drawer_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddEventActivity.class);
                intent.getBooleanExtra(AddEventActivity.IntentExtra_willAddEvent, true);
                startActivityForResult(intent, AddEventActivity.REQUEST_CODE_DidAddEvent);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fm = getSupportFragmentManager();
        mData = (RetainedMenuTabbedFragmet) fm.findFragmentByTag(TAG_RETAINED_MENU_FRAGMENT);

        // create the fragment and data the first time
        if (mData == null) {
            // add the fragment
            mData = new RetainedMenuTabbedFragmet();
            fm.beginTransaction().add(mData, TAG_RETAINED_MENU_FRAGMENT).commit();
            // load data from a data source or perform any calculation
            (new FirebaseConnection()).getUsers(new FirebaseConnection.GetUsersCallback() {
                @Override
                public void onSuccess(List<UserData> result) {

                    setupCurrentUser(result);

                }

                @Override
                public void onFail(String error) {
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                }
            });

            reloadEventData((ThisApp)getApplication(), new Handler(getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    reloadFragmentData();
                }
            });
        }else {
            //intitiate drawer menu with saved rounded image and name and maybe status...
            View hView =  navigationView.getHeaderView(0);
            TextView nav_user = hView.findViewById(R.id.userNameTextView);
            nav_user.setText(mData.drawerUserName);
            ImageView imageView = hView.findViewById(R.id.userProfilePicture);
            imageView.setImageDrawable(mData.drawable);
            TextView nav_userStatus = hView.findViewById(R.id.userStatus_TextView);
            nav_userStatus.setText(mData.userStatus);
        }

        if (activityReceiver != null) {
            IntentFilter intentFilter = new  IntentFilter("ACTION_STRING_ACTIVITY");
            registerReceiver(activityReceiver, intentFilter);
        }
    }

    //To receive messages from notification services
    private BroadcastReceiver activityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getBundleExtra(MESSAGE_TAG);
            MySnackbarProvider.showSnackBar(getCurrentFocus(),bundle.getString(MESSAGE_BODY_TAG));
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == AddEventActivity.REQUEST_CODE_DidAddEvent) {
                if (data.getBooleanExtra(AddEventActivity.IntentExtra_DidAddEvent, false)) {
                    reloadEventData((ThisApp)getApplication(), new Handler(getMainLooper()) {
                        @Override
                        public void handleMessage(Message msg) {
                            reloadFragmentData();
                        }
                    });
                }

            } else if (requestCode == CreateStatus.REQUEST_CODE_DidAddStatus) {
                if (data.getBooleanExtra(CreateStatus.IntentExtra_DidAddStatus, false)) {
                    mData.userStatus = data.getStringExtra(CreateStatus.IntentExtra_UserStatus);

                    reloadEventData((ThisApp)getApplication(), new Handler(getMainLooper()) {
                        @Override
                        public void handleMessage(Message msg) {
                            tabMapFragment.reloadUserData();
                            tabMapFragment.reloadMapMarkers();
                        }
                    });
                }

            }

        }
    }

    public static void reloadEventData(final ThisApp thisApp, final Handler handler) {
        (new FirebaseConnection()).getEvents(new FirebaseConnection.GetEventsCallback(){
            @Override
            public void onSuccess(List<EventData> result,List<String> keys){
                List<EventData> allEventData = new ArrayList<>();
                List<EventData> myEventData = new ArrayList<>();
                List<String> allEventDataKeys = new ArrayList<>();
                List<String> myEventDataKeys = new ArrayList<>();

                for (int i = 0; i < result.size(); i++) {

                    if(result.get(i).getCreatorID().equals(Profile.getCurrentProfile().getId())) {
                        myEventData.add(result.get(i));
                        myEventDataKeys.add(keys.get(i));
                    }
                    else {
                        allEventData.add(result.get(i));
                        allEventDataKeys.add(keys.get(i));
                    }
                }

                thisApp.setAllEvents(allEventData.toArray(new EventData[allEventData.size()]));
                thisApp.setMyEvents(myEventData.toArray(new EventData[myEventData.size()]));
                thisApp.setAllEventsKeys(allEventDataKeys.toArray(new String[allEventDataKeys.size()]));
                thisApp.setMyEventsKeys(myEventDataKeys.toArray(new String[myEventDataKeys.size()]));

                handler.dispatchMessage(new Message());
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(thisApp, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Fragment getFragment(int position){
        return getSupportFragmentManager().findFragmentByTag(getFragmentTag(position));
    }

    private String getFragmentTag(int position) {
        return "android:switcher:" + R.id.container + ":" + position;
    }

    private void setUserStatus()
    {
        userStatus_TextView = findViewById(R.id.userStatus_TextView);
        userStatus_TextView.setText(mData.userStatus);
    }

    private void reloadFragmentData() {

        setUserStatus();
        tabAllEventsFragment.reloadListData();
        tabMyEventsFragment.reloadListData();
    }

    private void checkShareLocation(Boolean isShareLocation)
    {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu menu = navigationView.getMenu();
        MenuItem nav_shareLocation = menu.findItem(R.id.nav_shareLocation);
        if(isShareLocation)
        {
            nav_shareLocation.setIcon(R.drawable.ic_location_on);
            nav_shareLocation.setTitle(R.string.shareLocationOnTitle);
        }
        else {
            nav_shareLocation.setIcon(R.drawable.ic_action_name);
            nav_shareLocation.setTitle(R.string.shareLocationOffTitle);
        }
    }

    //Checks if the current user is in the database then updates the drawerMenu profile picture and name
    private void setupCurrentUser(List<UserData> result)
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
        {
            for (int i = 0; i < result.size(); i++) {

                if (result.get(i).getFbID().equals(user.getUid()) )
                {
                    checkShareLocation(result.get(i).getShareLocation());

                    String profilePicUrl = result.get(i).getImgURLLarge();
                    String name = result.get(i).getName();

                    getBitmapFromURLAsync = new GetBitmapFromURLAsync();
                    getBitmapFromURLAsync.execute(profilePicUrl);
                    TextView textView = findViewById(R.id.userNameTextView);
                    textView.setText(name);
                    mData.drawerUserName = name;
                }
            }
        }

    }

    private void signOutUser(){
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(tabMapFragment, getResources().getString(R.string.Tabbed_Menu_Map));
        adapter.addFragment(tabAllEventsFragment, getResources().getString(R.string.Tabbed_Menu_All));
        adapter.addFragment(tabMyEventsFragment, getResources().getString(R.string.Tabbed_Menu_My));
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tabbed_view, menu);
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            }
            //We have permisson
            tabMapFragment.refreshUserLocationData();
        }
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.refresh_button) {

            MySnackbarProvider.showSnackBar(getCurrentFocus(),R.string.Refresh);


            if (viewPager.getCurrentItem() == 0) {
                tabMapFragment.refreshUserLocationData();
            }

            reloadEventData((ThisApp)getApplication(), new Handler(getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    if (viewPager.getCurrentItem() == 1) {
                        tabAllEventsFragment.reloadListData();
                    }
                    if (viewPager.getCurrentItem() == 2){
                        tabMyEventsFragment.reloadListData();
                    }
                }
            });


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setShareLocationON(final MenuItem item){
        (new FirebaseConnection()).UpdateUserShareLocation(true, new FirebaseConnection.GetDataCallback() {
            @Override
            public void onSuccess() {
                item.setIcon(R.drawable.ic_location_on);
                item.setTitle(R.string.shareLocationOnTitle);

                MySnackbarProvider.showSnackBar(getCurrentFocus(),R.string.shareLocationOnText);
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
            }
        });


    }

    private void setShareLocationOFF(final MenuItem item) {
        (new FirebaseConnection()).UpdateUserShareLocation(false, new FirebaseConnection.GetDataCallback() {
            @Override
            public void onSuccess() {
                item.setIcon(R.drawable.ic_action_name);
                item.setTitle(R.string.shareLocationOffTitle);

                MySnackbarProvider.showSnackBar(getCurrentFocus(),R.string.shareLocationOffText);
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_changeStatus) {
            Intent intent = new Intent(getApplicationContext(), CreateStatus.class);
            startActivityForResult(intent, CreateStatus.REQUEST_CODE_DidAddStatus);
        } else if (id == R.id.nav_shareLocation) {
            if(item.getTitle() == getString(R.string.shareLocationOffTitle))
            {
                //When user wants to share its location
                setShareLocationON(item);
            }
            else
            {
                //When user Don´t wants to share its location
                setShareLocationOFF(item);
            }


        } else if (id == R.id.nav_signOutButton) {

            signOutUser();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        if(getBitmapFromURLAsync != null){
            getBitmapFromURLAsync.cancel(true);
        }
        if (activityReceiver != null) {
            unregisterReceiver(activityReceiver);
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if(getBitmapFromURLAsync != null){
            getBitmapFromURLAsync.cancel(true);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int code = api.isGooglePlayServicesAvailable(this);
        if (code == ConnectionResult.SUCCESS) {
            // We are good to go
        } else {
            AlertDialog alertDialog =
                    new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert).setMessage(
                            R.string.play_services_message)
                            .create();
            alertDialog.show();
        }
    }


    private static Bitmap getBitmapFromURL(String imgUrl) {
        try {
            URL url = new URL(imgUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            return null;
        }
    }

    /**     AsyncTask to set user Profile image from url in drawer menu  */
    private class GetBitmapFromURLAsync extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            return getBitmapFromURL(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(),bitmap);
            drawable.setCircular(true);

            mData.drawable = drawable;
            ImageView imageView = findViewById(R.id.userProfilePicture);
            imageView.setImageDrawable(drawable);
        }
    }

}



