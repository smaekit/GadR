package com.marcusjakobsson.gadr;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlbratt on 2017-10-30.
 */

public class Tab_All_Events_Fragment extends Fragment {

    ListView listView;
    ListAdapter listViewAdapter;

    EventData[] allEventData;
    CustomListViewItem[] listData;
    private SwipeRefreshLayout swipeContainer;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_all_events,container,false);

        listView = (ListView) view.findViewById(R.id.ListView_AllEvents);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,long arg3) {
                Intent intent = new Intent(getContext(),DetailEventActivity.class);
                intent.putExtra(DetailEventActivity.EXTRA_EVENT_INDEX, position);
                startActivity(intent);

            }
        });


        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                reloadEventData();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.colorAccent,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);



        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        if(savedInstanceState == null)
        {
            reloadEventData();
        }
        reloadListData();



    }

    public void reloadEventData() {
        (new FirebaseConnection()).getEvents(new FirebaseConnection.EventsCallback(){
            @Override
            public void onSuccess(List<EventData> result){
                List<EventData> allEventData = new ArrayList<EventData>();
                List<EventData> myEventData = new ArrayList<EventData>();

                for (int i = 0; i < result.size(); i++) {




                    if(result.get(i).getCreatorID().equals(Profile.getCurrentProfile().getId())) {
                        myEventData.add(result.get(i));
                    }
                    else {
                        allEventData.add(result.get(i));
                    }
                }

                ((ThisApp) getActivity().getApplication()).setAllEvents((EventData[]) allEventData.toArray(new EventData[allEventData.size()]));
                ((ThisApp) getActivity().getApplication()).setMyEvents((EventData[]) myEventData.toArray(new EventData[myEventData.size()]));

                reloadListData();
                // Now we call setRefreshing(false) to signal refresh has finished
                showSnackBar(R.string.Refresh);
                swipeContainer.setRefreshing(false);

            }
        });
    }

    public void reloadListData() {
        //TODO: Add reload animation.


        EventData[] newAllEventData = ((ThisApp) getActivity().getApplication()).getAllEvents();

        if (newAllEventData != null && newAllEventData.length > 0) {
            listData = new CustomListViewItem[newAllEventData.length];
            allEventData = newAllEventData;

            for (int i = 0; i < listData.length; i++) {
                listData[i] = new CustomListViewItem(allEventData[i].getTitle(), allEventData[i].getStartTime() + " - " + allEventData[i].getEndTime(), allEventData[i].getLocationNickname(), allEventData[i].getCategoryIndex());
            }

            listViewAdapter = new CustomListViewAdapter(getActivity(), listData);
            listView.setAdapter(listViewAdapter);
        }else {
            loadDefault();
        }

    }

    private void loadDefault() {
        CustomListViewItem[] mListData = {new CustomListViewItem("Inga event", "", "",0) };
        listViewAdapter = new CustomListViewAdapter(getActivity(), mListData);
        listView.setAdapter(listViewAdapter);
    }

    public void showSnackBar(Integer stringID)
    {
        // make snackbar
        Snackbar mSnackbar = Snackbar.make(getActivity().getCurrentFocus(), stringID, Snackbar.LENGTH_LONG);
        // get snackbar view
        View mView = mSnackbar.getView();
        // get textview inside snackbar view
        TextView mTextView = (TextView) mView.findViewById(android.support.design.R.id.snackbar_text);
        mTextView.setTextColor(getResources().getColor(R.color.colorAccent,getActivity().getTheme()));
        mTextView.setTextSize(24);
        // set text to center
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            mTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        else
            mTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        // show the snackbar
        mSnackbar.show();
    }

}
