package com.marcusjakobsson.gadr;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.List;

/**
 * Created by carlbratt on 2017-10-30.
 */

public class Tab_All_Events_Fragment extends Fragment {

    String[] dummyData = {"Andreas", "Bengt" , "Carl" , "David" , "Erik", "Andreas", "Bengt" , "Carl" , "David" , "Andreas", "Bengt" , "Carl" , "David" , "Andreas", "Bengt" , "Carl" , "David" , "Andreas", "Bengt" , "Carl" , "David" , "Andreas", "Bengt" , "Carl" , "David" };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_all_events,container,false);

        CustomListViewItem[] dummyDataCustom = new CustomListViewItem[10];

        for (int i = 0; i < dummyDataCustom.length; i++) {
            dummyDataCustom[i] = new CustomListViewItem(dummyData[i], "00:00 - 01:00");
        }

        ListView listView = (ListView) view.findViewById(R.id.ListView_AllEvents);

        ListAdapter listViewAdapter = new CustomListViewAdapter(getActivity(), dummyDataCustom);
        listView.setAdapter(listViewAdapter);

        return view;
    }
}
