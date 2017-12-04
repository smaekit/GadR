package com.marcusjakobsson.gadr;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.R.attr.name;

public class MenuTabbedView extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MenuTabbedView";

    private SectionsPagerAdapter sectionsPageAdapter;

    private ViewPager viewPager;

    private FirebaseConnection firebaseConnection;

    TextView userStatus_TextView;


    //Fragments
    Tab_Map_Fragment tabMapFragment = new Tab_Map_Fragment();
    Tab_All_Events_Fragment tabAllEventsFragment = new Tab_All_Events_Fragment();
    Tab_My_Events_Fragment tabMyEventsFragment = new Tab_My_Events_Fragment();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed_view);



        sectionsPageAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setOffscreenPageLimit(3);  //How many screens before reload
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);




        Toolbar toolbar = (Toolbar) findViewById(R.id.top_drawer_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
  //                      .setAction("Action", null).show();
                Intent intent = new Intent(getApplicationContext(), AddEventActivity.class);
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


        //Firebase
        FirebaseConnection fc = new FirebaseConnection();


/*        for (int i = 0; i< 5; i++) {
            fc.AddUser(new UserData("ID" + Integer.toString(i),
                    "Name" + Integer.toString(i),
                    i * 10,
                    "LargeURL" + Integer.toString(i),
                    "SmallURL" + Integer.toString(i)));
        }*/




       fc.getUsers(new FirebaseConnection.UsersCallback(){
            @Override
            public void onSuccess(List<UserData> result){

                setupCurrentUser(result);
                reloadEventData();

            }
        });







/*        for (int i = 0; i< 5; i++) {
            fc.AddEvent(new EventData(
                    "creId" + Integer.toString(i),
                    "EventTitle" + Integer.toString(i),
                    "Bla bla bla desc " + Integer.toString(i),
                    new CustomLocation(57.7824464, 14.176048900000069),
                    new Date())
            );
        }*/



       /*for (int i = 0; i< 5; i++) {
            fc.AddStatus(new StatusData(
                    "creID" +Integer.toString(i),
                    "This is a cool status to have",
                    new CustomLocation(57.7824464, 14.176048900000069),
                    new Date()
            ));
        }*/


/*        fc.getStatus(new FirebaseConnection.StatusCallback(){
            @Override
            public void onSuccess(List<StatusData> result){

                for (int i = 0; i < result.size(); i++) {
                    Log.i(TAG, DateFormat.getDateTimeInstance().format(result.get(i).getDate()));
                }

            }
        });*/




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == AddEventActivity.REQUEST_CODE_DidAddEvent) {
                Boolean b = data.getBooleanExtra(AddEventActivity.IntentExtra_DidAddEvent, false);
                if (b) {
                    Log.i(TAG, "True");
                    reloadEventData();
                }
                else { Log.i(TAG, "False"); }
            } else if (requestCode == CreateStatus.REQUEST_CODE_DidAddStatus) {
                Boolean b = data.getBooleanExtra(CreateStatus.IntentExtra_DidAddStatus, false);
                if (b) {
                    Log.i(TAG, "True");
                    String status = data.getStringExtra(CreateStatus.IntentExtra_UserStatus);
                    userStatus_TextView = (TextView)findViewById(R.id.userStatus_TextView);
                    userStatus_TextView.setText(status);
                    reloadEventData();
                }
                else { Log.i(TAG, "False"); }
            }

        }
    }

    private void reloadEventData() {
        (new FirebaseConnection()).getEvents(new FirebaseConnection.EventsCallback(){
            @Override
            public void onSuccess(List<EventData> result){
                List<EventData> allEventData = new ArrayList<EventData>();
                List<EventData> myEventData = new ArrayList<EventData>();



                for (int i = 0; i < result.size(); i++) {
                    Log.i(TAG, "ID:          " + result.get(i).getCreatorID().equals(Profile.getCurrentProfile().getId()));



                    if(result.get(i).getCreatorID().equals(Profile.getCurrentProfile().getId())) {
                        myEventData.add(result.get(i));
                    }
                    else {
                        allEventData.add(result.get(i));
                    }
                }

                ((ThisApp) getApplication()).setAllEvents((EventData[]) allEventData.toArray(new EventData[allEventData.size()]));
                ((ThisApp) getApplication()).setMyEvents((EventData[]) myEventData.toArray(new EventData[myEventData.size()]));

                reloadFragmentData();
            }
        });
    }


    private void reloadFragmentData() {

        tabAllEventsFragment.reloadListData();
        tabMapFragment.reloadUserData();
//        tabMapFragment.reloadEventMarkers();
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
                    Log.i(TAG,"found the right user lets get his picture");
                    String profilePicUrl = result.get(i).getImgURLLarge();
                    String name = result.get(i).getName();

                    new DownloadImageTask((ImageView)findViewById(R.id.userProfilePicture)).execute(profilePicUrl);
                    TextView textView = (TextView)findViewById(R.id.userNameTextView);
                    textView.setText(name);
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

//
//
//    /**
//     * The {@link android.support.v4.view.PagerAdapter} that will provide
//     * fragments for each of the sections. We use a
//     * {@link FragmentPagerAdapter} derivative, which will keep every
//     * loaded fragment in memory. If this becomes too memory intensive, it
//     * may be best to switch to a
//     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
//     */
//    private SectionsPagerAdapter mSectionsPagerAdapter;
//
//    /**
//     * The {@link ViewPager} that will host the section contents.
//     */
//    private ViewPager mViewPager;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_tabbed_view);
//
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        // Create the adapter that will return a fragment for each of the three
//        // primary sections of the activity.
//        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
//
//        // Set up the ViewPager with the sections adapter.
//        mViewPager = (ViewPager) findViewById(R.id.container);
//        mViewPager.setAdapter(mSectionsPagerAdapter);
//
//        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
//        tabLayout.setupWithViewPager(mViewPager);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_tabbed_view, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    /**
//     * A placeholder fragment containing a simple view.
//     */
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
        if (id == R.id.action_settings) {
            reloadEventData();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                firebaseConnection2.UpdateUserShareLocation(false);
                item.setIcon(R.drawable.ic_location_on);
                item.setTitle(R.string.shareLocationOnTitle);
                Toast.makeText(getApplicationContext(), R.string.shareLocationOffText, Toast.LENGTH_SHORT).show();
            }
            else
            {
                FirebaseConnection firebaseConnection2 = new FirebaseConnection();
                firebaseConnection2.UpdateUserShareLocation(true);
                item.setIcon(R.drawable.ic_action_name);
                item.setTitle(R.string.shareLocationOffTitle);
                Toast.makeText(getApplicationContext(), R.string.shareLocationOnText, Toast.LENGTH_SHORT).show();
            }


        } else if (id == R.id.nav_slideshow) {


        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_signOutButton) {

            signOutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

}



