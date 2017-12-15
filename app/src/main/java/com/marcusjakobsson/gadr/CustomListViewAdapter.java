package com.marcusjakobsson.gadr;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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

        if(convertView == null){
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.custom_list_row, parent, false);
        }


        CustomListViewItem singleItem = getItem(position);
        TextView title = (TextView) convertView.findViewById(R.id.textView_title);
        TextView time = (TextView) convertView.findViewById(R.id.textView_time);
        TextView place = (TextView) convertView.findViewById(R.id.textView_place);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.category_ImageView);


        title.setText(singleItem.getTitle());
        time.setText(singleItem.getTime());
        place.setText(singleItem.getPlace());
        imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), Category.categoryImageIndex[singleItem.getCategoryIndex()]));

        return convertView;
    }


}
