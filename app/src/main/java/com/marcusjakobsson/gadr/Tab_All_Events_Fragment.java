package com.marcusjakobsson.gadr;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by carlbratt on 2017-10-30.
 */

public class Tab_All_Events_Fragment extends Fragment {

    ListView listView;
    ListAdapter listViewAdapter;

    EventData[] allEventData;
    CustomListViewItem[] listData;

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
                Intent intent = new Intent(getContext(),DetailEventActivity.class);
                intent.putExtra(DetailEventActivity.EXTRA_EVENT_INDEX, position);
                startActivity(intent);

            }
        });

        return view;
    }


    public void reloadListData() {
        //TODO: Add reload animation.


        EventData[] newAllEventData = ((ThisApp) getActivity().getApplication()).getAllEvents();

        if (newAllEventData != null) {
            listData = new CustomListViewItem[newAllEventData.length];
            allEventData = newAllEventData;

            for (int i = 0; i < listData.length; i++) {
                listData[i] = new CustomListViewItem(allEventData[i].getTitle(), allEventData[i].getStartTime() + " - " + allEventData[i].getEndTime(), allEventData[i].getLocationNickname(), allEventData[i].getCategoryIndex());
            }

            listViewAdapter = new CustomListViewAdapter(getActivity(), listData);
            listView.setAdapter(listViewAdapter);
        }
        Toast.makeText(getContext(), "all event reload list", Toast.LENGTH_SHORT).show();
    }

    private void loadDefault() {
        CustomListViewItem[] mListData = {new CustomListViewItem("Inga event", "", "",0) };
        listViewAdapter = new CustomListViewAdapter(getActivity(), mListData);
        listView.setAdapter(listViewAdapter);
    }

}
