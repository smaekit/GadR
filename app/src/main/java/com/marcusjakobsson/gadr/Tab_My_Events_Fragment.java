package com.marcusjakobsson.gadr;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
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

public class Tab_My_Events_Fragment extends Fragment {

    ListView listView;
    ListAdapter listViewAdapter;

    EventData[] myEventData;
    CustomListViewItem[] listData;
    public SwipeRefreshLayout swipeContainer;

    boolean listHasData = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_my_events,container,false);

        listView = (ListView) view.findViewById(R.id.ListView_MyEvents);
        loadDefault();

        reloadListData();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,long arg3) {
                if (listHasData) {
                    Intent intent = new Intent(getContext(), AddEventActivity.class);
                    intent.putExtra(AddEventActivity.EXTRA_EVENT_INDEX, position);
                    startActivity(intent);
                }

            }
        });

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.mySwipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                MenuTabbedView.reloadEventData((ThisApp)getActivity().getApplication(), new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        reloadListData();

                        swipeContainer.setRefreshing(false);
                    }
                });
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        return view;
    }

    public void reloadListData() {
        EventData[] newAllEventData = ((ThisApp) getActivity().getApplication()).getMyEvents();

        if (newAllEventData != null) {
            listData = new CustomListViewItem[newAllEventData.length];
            myEventData = newAllEventData;

            for (int i = 0; i < listData.length; i++) {
                listData[i] = new CustomListViewItem(myEventData[i].getTitle(), myEventData[i].getStartTime() + " - " + myEventData[i].getEndTime(), myEventData[i].getLocationNickname(), myEventData[i].getCategoryIndex());
            }

            listViewAdapter = new CustomListViewAdapter(getActivity(), listData);
            listView.setAdapter(listViewAdapter);

            listHasData = true;
        }
        else {
            loadDefault();
        }


        Toast.makeText(getContext(), "my event reload list", Toast.LENGTH_SHORT).show();
    }

    private void loadDefault() {
        CustomListViewItem[] mListData = {new CustomListViewItem("Inga event", "", "",0) };
        listViewAdapter = new CustomListViewAdapter(getActivity(), mListData);
        listView.setAdapter(listViewAdapter);

        listHasData = false;
    }
}
