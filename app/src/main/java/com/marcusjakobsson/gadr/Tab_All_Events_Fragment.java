package com.marcusjakobsson.gadr;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
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
        swipeContainer.setColorSchemeResources(R.color.colorAccent,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);



        return view;
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

    }

    private void loadDefault() {
        CustomListViewItem[] mListData = {new CustomListViewItem("Inga event", "", "",0) };
        listViewAdapter = new CustomListViewAdapter(getActivity(), mListData);
        listView.setAdapter(listViewAdapter);

        listHasData = false;
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
