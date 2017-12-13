package com.marcusjakobsson.gadr;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.Profile;

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

    boolean listHasData = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_all_events,container,false);

        listView = (ListView) view.findViewById(R.id.ListView_AllEvents);
        loadDefault();

        reloadListData();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,long arg3) {
                if (listHasData) {
                    Intent intent = new Intent(getContext(), DetailEventActivity.class);
                    intent.putExtra(DetailEventActivity.EXTRA_EVENT_INDEX, position);
                    startActivity(intent);
                }

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
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);



        return view;
    }




    public void reloadEventData() {
        (new FirebaseConnection()).getEvents(new FirebaseConnection.EventsCallback(){
            @Override
            public void onSuccess(List<EventData> result, List<String> keys){
                List<EventData> allEventData = new ArrayList<EventData>();
                List<EventData> myEventData = new ArrayList<EventData>();
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

                ((ThisApp) getActivity().getApplication()).setAllEvents((EventData[]) allEventData.toArray(new EventData[allEventData.size()]));
                ((ThisApp) getActivity().getApplication()).setMyEvents((EventData[]) myEventData.toArray(new EventData[myEventData.size()]));
                ((ThisApp) getActivity().getApplication()).setAllEventsKeys((String[]) allEventDataKeys.toArray(new String[allEventDataKeys.size()]));
                ((ThisApp) getActivity().getApplication()).setMyEventsKeys((String[]) myEventDataKeys.toArray(new String[myEventDataKeys.size()]));


                reloadListData();
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);

            }
        });
    }

    public void reloadListData() {
        EventData[] newAllEventData = ((ThisApp) getActivity().getApplication()).getAllEvents();

        if (newAllEventData != null && newAllEventData.length > 0) {
            listData = new CustomListViewItem[newAllEventData.length];
            allEventData = newAllEventData;

            for (int i = 0; i < listData.length; i++) {
                listData[i] = new CustomListViewItem(allEventData[i].getTitle(), allEventData[i].getStartTime() + " - " + allEventData[i].getEndTime(), allEventData[i].getLocationNickname(), allEventData[i].getCategoryIndex());
            }

            listViewAdapter = new CustomListViewAdapter(getActivity(), listData);
            listView.setAdapter(listViewAdapter);

            listHasData = true;
        }
        else {
            loadDefault();
        }

        Toast.makeText(getContext(), "all event reload list", Toast.LENGTH_SHORT).show();
    }

    private void loadDefault() {
        CustomListViewItem[] mListData = {new CustomListViewItem("Inga event", "", "",0) };
        listViewAdapter = new CustomListViewAdapter(getActivity(), mListData);
        listView.setAdapter(listViewAdapter);

        listHasData = false;
    }

}
