package com.marcusjakobsson.gadr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by carlbratt on 2017-10-30.
 */

public class Tab_My_Events_Fragment extends Fragment {

    ListView listView;
    ListAdapter listViewAdapter;

    EventData[] myEventData;
    CustomListViewItem[] listData;

    String[] dummyData = {"Andreas", "Bengt" , "Carl" , "David" , "Erik", "Andreas", "Bengt" , "Carl" , "David" , "Andreas", "Bengt" , "Carl" , "David" , "Andreas", "Bengt" , "Carl" , "David" , "Andreas", "Bengt" , "Carl" , "David" , "Andreas", "Bengt" , "Carl" , "David" };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_my_events,container,false);

        listData = new CustomListViewItem[10];

        for (int i = 0; i < listData.length; i++) {
            listData[i] = new CustomListViewItem(dummyData[i], "00:00 - 01:00");
        }

        listView = (ListView) view.findViewById(R.id.ListView_MyEvents);

        listViewAdapter = new CustomListViewAdapter(getActivity(), listData);
        listView.setAdapter(listViewAdapter);

        return view;
    }


    public void reloadListData() {
        //TODO: Add reload animation.

        EventData[] newAllEventData = ((ThisApp) getActivity().getApplication()).getMyEvents();

        if (newAllEventData != null) {
            listData = new CustomListViewItem[newAllEventData.length];
            myEventData = newAllEventData;

            for (int i = 0; i < listData.length; i++) {
                listData[i] = new CustomListViewItem(myEventData[i].getTitle(), myEventData[i].getStartTime() + " - " + myEventData[i].getEndTime());
            }

            listViewAdapter = new CustomListViewAdapter(getActivity(), listData);
            listView.setAdapter(listViewAdapter);
        }
    }


}
