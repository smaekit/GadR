package com.marcusjakobsson.gadr;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static android.R.attr.name;
import static android.R.attr.theme;
import static android.support.v4.view.PagerAdapter.POSITION_NONE;

public class MenuTabbedView extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MenuTabbedView";

    private SectionsPagerAdapter sectionsPageAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    GetBitmapFromURLAsync getBitmapFromURLAsync;
    private static final String TAG_RETAINED_MENU_FRAGMENT = "RetainedMenuTabbedFragment";
    private RetainedMenuTabbedFragmet mData;

    TextView userStatus_TextView;

    String userStatus;

    //Fragments
    Tab_Map_Fragment tabMapFragment;
    Tab_All_Events_Fragment tabAllEventsFragment;
    Tab_My_Events_Fragment tabMyEventsFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed_view);

        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int code = api.isGooglePlayServicesAvailable(this);
        if (code == ConnectionResult.SUCCESS) {
            // Do Your Stuff Here
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

        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setOffscreenPageLimit(3);  //How many screens before reload
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);





        // If device is running SDK < 23
        if (Build.VERSION.SDK_INT < 23)
        {
            //We can just request locationUpdates
        }else
        {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //Ask for permisson
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            else
            {
                //We have permisson
                Log.i(TAG, "we have permisson");
            }
        }



        Toolbar toolbar = (Toolbar) findViewById(R.id.top_drawer_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddEventActivity.class);
                intent.getBooleanExtra(AddEventActivity.IntentExtra_willAddEvent, true);
                startActivityForResult(intent, AddEventActivity.REQUEST_CODE_DidAddEvent);
            }
        });



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);




        FragmentManager fm = getSupportFragmentManager();
        mData = (RetainedMenuTabbedFragmet) fm.findFragmentByTag(TAG_RETAINED_MENU_FRAGMENT);

        // create the fragment and data the first time
        if (mData == null) {
            // add the fragment
            mData = new RetainedMenuTabbedFragmet();
            fm.beginTransaction().add(mData, TAG_RETAINED_MENU_FRAGMENT).commit();
            // load data from a data source or perform any calculation
            (new FirebaseConnection()).getUsers(new FirebaseConnection.UsersCallback() {
                @Override
                public void onSuccess(List<UserData> result) {

                    setupCurrentUser(result);
                    //reloadEventData((ThisApp)getApplication(), new Handler());

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
            TextView nav_user = (TextView)hView.findViewById(R.id.userNameTextView);
            nav_user.setText(mData.drawerUserName);
            ImageView imageView = (ImageView)hView.findViewById(R.id.userProfilePicture);
            imageView.setImageDrawable(mData.drawable);
            TextView nav_userStatus = (TextView)hView.findViewById(R.id.userStatus_TextView);
            nav_userStatus.setText(mData.userStatus);

            //reloadEventData();
            //tabMapFragment.reloadUserData();



        }




        if (activityReceiver != null) {
            IntentFilter intentFilter = new  IntentFilter("ACTION_STRING_ACTIVITY");
            registerReceiver(activityReceiver, intentFilter);
        }



    }

    private BroadcastReceiver activityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //TextView textview = (TextView) findViewById(R.id.textview);
            Bundle bundle = intent.getBundleExtra("msg");
            showSnackBar(bundle.getString("msgBody"));
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
                else { Log.i(TAG, "False"); }
            }

        }
    }

    public static void reloadEventData(final ThisApp thisApp, final Handler handler) {
        (new FirebaseConnection()).getEvents(new FirebaseConnection.EventsCallback(){
            @Override
            public void onSuccess(List<EventData> result,List<String> keys){
                List<EventData> allEventData = new ArrayList<>();
                List<EventData> myEventData = new ArrayList<>();
                List<String> allEventDataKeys = new ArrayList<>();
                List<String> myEventDataKeys = new ArrayList<>();

                for (int i = 0; i < result.size(); i++) {
                    Log.i(TAG, "ID:          " + result.get(i).getCreatorID().equals(Profile.getCurrentProfile().getId()));

                    if(result.get(i).getCreatorID().equals(Profile.getCurrentProfile().getId())) {
                        myEventData.add(result.get(i));
                        myEventDataKeys.add(keys.get(i));
                    }
                    else {
                        allEventData.add(result.get(i));
                        allEventDataKeys.add(keys.get(i));
                    }
                }

                thisApp.setAllEvents((EventData[]) allEventData.toArray(new EventData[allEventData.size()]));
                thisApp.setMyEvents((EventData[]) myEventData.toArray(new EventData[myEventData.size()]));
                thisApp.setAllEventsKeys(allEventDataKeys.toArray(new String[allEventDataKeys.size()]));
                thisApp.setMyEventsKeys(myEventDataKeys.toArray(new String[myEventDataKeys.size()]));



                handler.dispatchMessage(new Message());
                //reloadFragmentData();
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
        userStatus_TextView = (TextView)findViewById(R.id.userStatus_TextView);
        userStatus_TextView.setText(mData.userStatus);
    }

    private void reloadFragmentData() {

        //Todo reload current fragmet?
        setUserStatus();
        tabAllEventsFragment.reloadListData();
        tabMyEventsFragment.reloadListData();
        //tabMapFragment.reloadUserData();
        //tabMapFragment.reloadMapMarkers();
    }


    public void checkShareLocation(Boolean isShareLocation)
    {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
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
                Log.i(TAG,"searching");
                Log.i(TAG,result.get(i).getFbID());
                Log.i(TAG,user.getUid());
                if (result.get(i).getFbID().equals(user.getUid()) )
                {
                    checkShareLocation(result.get(i).getShareLocation());

                    Log.i(TAG,"found the right user lets get his picture");
                    String profilePicUrl = result.get(i).getImgURLLarge();
                    String name = result.get(i).getName();

                    //new DownloadImageTask((ImageView)findViewById(R.id.userProfilePicture)).execute(profilePicUrl);
                    getBitmapFromURLAsync = new GetBitmapFromURLAsync();
                    getBitmapFromURLAsync.execute(profilePicUrl);
                    TextView textView = (TextView)findViewById(R.id.userNameTextView);
                    textView.setText(name);
                    mData.drawerUserName = name;
                }
            }
        }

    }

    void signOutButton(View view)
    {
        signOutUser();
    }
    void signOutUser(){
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
            try {
                //refresh fragments
                viewPager.getAdapter().notifyDataSetChanged();
            }catch (NullPointerException e)
            {
                e.printStackTrace();
            }


        }
    }

    //Todo what is this?
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        //noinspection SimplifiableIfStatement
        if (id == R.id.refresh_button) {

            showSnackBar(R.string.Refresh);


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

    public void showSnackBar(Integer stringID)
    {
        // make snackbar
        Snackbar mSnackbar = Snackbar.make(getCurrentFocus(), stringID, Snackbar.LENGTH_LONG);
        // get snackbar view
        View mView = mSnackbar.getView();
        // get textview inside snackbar view
        TextView mTextView = (TextView) mView.findViewById(android.support.design.R.id.snackbar_text);
        mTextView.setTextColor(getResources().getColor(R.color.colorAccent,getTheme()));
        mTextView.setTextSize(24);
        // set text to center
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            mTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        else
            mTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        // show the snackbar
        mSnackbar.show();
    }

    public void showSnackBar(String message)
    {
        // make snackbar
        Snackbar mSnackbar = Snackbar.make(getCurrentFocus(), message, Snackbar.LENGTH_LONG);
        // get snackbar view
        View mView = mSnackbar.getView();
        // get textview inside snackbar view
        TextView mTextView = (TextView) mView.findViewById(android.support.design.R.id.snackbar_text);
        mTextView.setTextColor(getResources().getColor(R.color.colorAccent,getTheme()));
        mTextView.setTextSize(24);
        // set text to center
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            mTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        else
            mTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        // show the snackbar
        mSnackbar.show();
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
                FirebaseConnection firebaseConnection2 = new FirebaseConnection();
                firebaseConnection2.UpdateUserShareLocation(true);
                item.setIcon(R.drawable.ic_location_on);
                item.setTitle(R.string.shareLocationOnTitle);
                //Toast.makeText(getApplicationContext(), R.string.shareLocationOnText, Toast.LENGTH_SHORT).show();
                showSnackBar(R.string.shareLocationOnText);

            }
            else
            {
                FirebaseConnection firebaseConnection2 = new FirebaseConnection();
                firebaseConnection2.UpdateUserShareLocation(false);
                item.setIcon(R.drawable.ic_action_name);
                item.setTitle(R.string.shareLocationOffTitle);
                //Toast.makeText(getApplicationContext(), R.string.shareLocationOffText, Toast.LENGTH_SHORT).show();
                showSnackBar(R.string.shareLocationOffText);
            }


        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent(getApplicationContext(), DetailEventActivity.class);
            startActivity(intent);


        } else if (id == R.id.nav_manage) {
//            FirebaseMessaging.getInstance().subscribeToTopic("news");
//            Toast.makeText(this, "Notifications ON", Toast.LENGTH_SHORT).show();
//            if (false)
//            {
//                FirebaseMessaging.getInstance().unsubscribeFromTopic("news");
//                Toast.makeText(this, "Notifications OFF", Toast.LENGTH_SHORT).show();
//
//            }


        } else if (id == R.id.nav_share) {
//            MyFirebaseMessagingService myFirebaseMessagingService = new MyFirebaseMessagingService();
//            FirebaseMessaging fm = FirebaseMessaging.getInstance();
//            String SENDER_ID = "21598535641";
//            AtomicInteger msgId = new AtomicInteger();
//            msgId.incrementAndGet();
//            fm.send(new RemoteMessage.Builder(SENDER_ID + "@gcm.googleapis.com")
//                    .setMessageId(msgId.toString())
//                    .addData("my_message", "Hello World")
//                    .addData("my_action","SAY_HELLO")
//                    .build());

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_signOutButton) {

            signOutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Todo destroy asyncTasks and objects when view is getting onDestroy
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
            // Do Your Stuff Here
        } else {
            AlertDialog alertDialog =
                    new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert).setMessage(
                            "You need to download Google Play Services in order to use this part of the application")
                            .create();
            alertDialog.show();
        }
    }

    //Async task to set user Profileimage from url in drawer menu
    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        //private ImageView bmImage;
        private WeakReference<ImageView> imageViewReference;

        private DownloadImageTask(ImageView bmImage) {
            imageViewReference = new WeakReference<>(bmImage);
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {

            ImageView imageView = imageViewReference.get();
            if(imageView == null){return;}

            RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create( imageView.getResources(),result);
            drawable.setCircular(true);
            imageView.setImageDrawable(drawable);
        }
    }


    public static Bitmap getBitmapFromURL(String imgUrl) {
        try {
            URL url = new URL(imgUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    /**     AsyncTAsk for Image Bitmap  */
    private class GetBitmapFromURLAsync extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            return getBitmapFromURL(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

//            if(imageView == null){return;}

            RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(),bitmap);
            drawable.setCircular(true);

            mData.drawable = drawable;
            ImageView imageView = (ImageView)findViewById(R.id.userProfilePicture);
            imageView.setImageDrawable(drawable);
        }
    }

}



