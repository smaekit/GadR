package com.marcusjakobsson.gadr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by carlbratt on 2017-11-07.
 */

public class CustomListViewAdapter extends ArrayAdapter<CustomListViewItem>{

    public CustomListViewAdapter(Context context, CustomListViewItem[] listArr) {
        super(context, R.layout.custom_list_row, listArr);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.custom_list_row, parent, false);

        CustomListViewItem singleItem = getItem(position);
        TextView title = (TextView) view.findViewById(R.id.textView_title);
        TextView time = (TextView) view.findViewById(R.id.textView_time);

        title.setText(singleItem.getTitle());
        time.setText(singleItem.getTime());

        return view;
    }
}
