package com.marcusjakobsson.gadr;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.Inet4Address;

public class DetailEventActivity extends AppCompatActivity {

    static final String EXTRA_EVENT_INDEX = "event_index";

    private ImageView imageView;
    private TextView title_textView;
    private TextView place_textView;
    private TextView time_textView;
    private TextView date_textView;
    private TextView description_textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_event);

        imageView = (ImageView) findViewById(R.id.category_ImageView);
        title_textView = (TextView) findViewById(R.id.title_TextView);
        place_textView = (TextView) findViewById(R.id.place_TextView);
        time_textView = (TextView) findViewById(R.id.time_TextView);
        date_textView = (TextView) findViewById(R.id.date_TextView);
        description_textView = (TextView) findViewById(R.id.description_TextView);


        Intent intent = getIntent();
        EventData eventData = ((ThisApp) getApplication()).getAllEventByIndex(getIntent().getIntExtra(EXTRA_EVENT_INDEX, 0) );


        imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), Category.categoryImageIndex[eventData.getCategoryIndex()]));
        title_textView.setText(eventData.getTitle());
        place_textView.setText(eventData.getLocationNickname());
        time_textView.setText(eventData.getStartTime() + R.string.TimeConnection + eventData.getEndTime());
        date_textView.setText(eventData.getDate());
        description_textView.setText(eventData.getDescription());
    }

    public void back_button(View view)
    {
        finish();
    }
}
