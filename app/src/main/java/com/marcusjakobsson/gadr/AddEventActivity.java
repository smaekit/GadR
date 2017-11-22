package com.marcusjakobsson.gadr;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.facebook.AccessToken;

public class AddEventActivity extends AppCompatActivity {

    private static final String TAG = "MenuTabbedView";

    public static final int REQUEST_CODE_DidAddEvent = 1;
    public static final String IntentExtra_DidAddEvent = "didAddEvent";

    EditText title_EditText;
    EditText description_EditText;
    EditText date_EditText;
    EditText time_EditText;

    Calendar calendar;
    DatePickerDialog.OnDateSetListener dateListener;
    TimePickerDialog.OnTimeSetListener timeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        title_EditText = (EditText) findViewById(R.id.EditText_Title);
        description_EditText = (EditText) findViewById(R.id.EditText_Description);
        date_EditText = (EditText) findViewById(R.id.EditText_Date);
        time_EditText = (EditText) findViewById(R.id.EditText_Time);

        calendar = Calendar.getInstance();

        dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view,
                                  int year,
                                  int monthOfYear,
                                  int dayOfMonth) {

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateDateLabel();
            }

        };

        date_EditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AddEventActivity.this,
                        dateListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

                datePickerDialog.show();
            }
        });

        timeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                updateTimeLabel();
            }
        };


        time_EditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();

                new TimePickerDialog(
                        AddEventActivity.this,
                        timeListener,
                        calendar.get(Calendar.HOUR),
                        calendar.get(Calendar.MINUTE),
                        true).show();
            }
        });
    }

    private void updateDateLabel() {
        String customFormat = "dd/MM - yy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(customFormat, Locale.ENGLISH);

        date_EditText.setText(simpleDateFormat.format(calendar.getTime()));
    }

    private void updateTimeLabel() {
        String customFormat = "HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(customFormat, Locale.US);
        time_EditText.setText(simpleDateFormat.format(calendar.getTime()));

    }


    //TODO: change to listener
    public void addEvent(View v) {
        if (fieldsAreValid()) {
            // Create Event-data and send to Firebase.
            EventData eventData = new EventData(
                    AccessToken.USER_ID_KEY,
                    title_EditText.getText().toString(),
                    description_EditText.getText().toString(),
                    new CustomLocation(), //Todo: get location
                    new Date()          //TODO: set correct dateListener and time
            );

            FirebaseConnection firebaseConnection = new FirebaseConnection();
            firebaseConnection.AddEvent(eventData);

            Intent intent = new Intent();
            intent.putExtra(IntentExtra_DidAddEvent, true);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private boolean fieldsAreValid() {
        //Check if input are empty or invalid.
        boolean fieldsOK = true;

        if (fieldIsEmpty(title_EditText)) { fieldsOK = false; }

        if (fieldIsEmpty(description_EditText)) { fieldsOK = false; }

        if (fieldIsEmpty(date_EditText)) { fieldsOK = false; }

        if (fieldIsEmpty(time_EditText)) { fieldsOK = false; }

        return fieldsOK;
    }

    private boolean fieldIsEmpty(EditText editText) {
        if (TextUtils.isEmpty(title_EditText.getText().toString())) {
            editText.setHintTextColor(getResources().getColor(R.color.com_facebook_blue));
            return true;
        }
        return false;
    }

    private void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
