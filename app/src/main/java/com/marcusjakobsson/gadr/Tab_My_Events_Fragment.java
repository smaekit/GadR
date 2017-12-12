package com.marcusjakobsson.gadr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by carlbratt on 2017-10-30.
 */

public class Tab_My_Events_Fragment extends Fragment {

    ListView listView;
    ListAdapter listViewAdapter;

    EventData[] myEventData;
    CustomListViewItem[] listData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_my_events,container,false);

        listView = (ListView) view.findViewById(R.id.ListView_MyEvents);
        loadDefault();

        reloadListData();

        return view;
    }

    public void reloadListData() {
        //TODO: Add reload animation.


        EventData[] newAllEventData = ((ThisApp) getActivity().getApplication()).getMyEvents();

        if (newAllEventData != null) {
            listData = new CustomListViewItem[newAllEventData.length];
            myEventData = newAllEventData;

            for (int i = 0; i < listData.length; i++) {
                listData[i] = new CustomListViewItem(myEventData[i].getTitle(), myEventData[i].getStartTime() + " - " + myEventData[i].getEndTime(), myEventData[i].getLocationNickname(), myEventData[i].getCategoryIndex());
            }

            listViewAdapter = new CustomListViewAdapter(getActivity(), listData);
            listView.setAdapter(listViewAdapter);
        }
        Toast.makeText(getContext(), "my event reload list", Toast.LENGTH_SHORT).show();
    }

    private void loadDefault() {
        CustomListViewItem[] mListData = {new CustomListViewItem("Inga event", "", "",0) };
        listViewAdapter = new CustomListViewAdapter(getActivity(), mListData);
        listView.setAdapter(listViewAdapter);
    }
}
