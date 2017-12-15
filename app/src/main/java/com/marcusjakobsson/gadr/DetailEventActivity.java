package com.marcusjakobsson.gadr;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailEventActivity extends AppCompatActivity {

    static final String EXTRA_EVENT_INDEX = "event_index";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_event);

        ImageView imageView = findViewById(R.id.category_ImageView);
        TextView title_textView = findViewById(R.id.title_TextView);
        TextView place_textView = findViewById(R.id.place_TextView);
        TextView time_textView = findViewById(R.id.time_TextView);
        TextView date_textView = findViewById(R.id.date_TextView);
        TextView description_textView = findViewById(R.id.description_TextView);


        EventData eventData = ((ThisApp) getApplication()).getAllEventByIndex(getIntent().getIntExtra(EXTRA_EVENT_INDEX, 0) );


        imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), Category.categoryImageIndex[eventData.getCategoryIndex()]));
        title_textView.setText(eventData.getTitle());
        place_textView.setText(eventData.getLocationNickname());
        time_textView.setText(eventData.getStartTime() + getString(R.string.TimeConnection) + eventData.getEndTime());
        date_textView.setText(eventData.getDate());
        description_textView.setText(eventData.getDescription());
    }

    public void back_button(View view)
    {
        finish();
    }
}
