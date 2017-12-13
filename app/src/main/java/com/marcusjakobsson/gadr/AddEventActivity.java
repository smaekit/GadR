package com.marcusjakobsson.gadr;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.EventLogTags;
import android.util.Log;
import android.util.TimeUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.*;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

public class AddEventActivity extends AppCompatActivity {

    private static final String TAG = "AddEventActivity";

    public static final int REQUEST_CODE_DidAddEvent = 1;
    public static final String IntentExtra_DidAddEvent = "didAddEvent";

    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 5;

    EditText title_EditText;
    EditText description_EditText;
    EditText date_EditText;
    EditText startTime_EditText;
    EditText endTime_EditText;
    EditText location_EditText;
    EditText locationNickname_EditText;
    Spinner category_Spinner;

    Calendar calendar;
    Calendar endCalendar;
    DatePickerDialog.OnDateSetListener dateListener;
    TimePickerDialog.OnTimeSetListener startTimeListener;
    TimePickerDialog.OnTimeSetListener endTimeListener;

    CustomLocation customLocation = new CustomLocation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        title_EditText = (EditText) findViewById(R.id.EditText_Title);
        description_EditText = (EditText) findViewById(R.id.EditText_Description);
        date_EditText = (EditText) findViewById(R.id.EditText_Date);
        startTime_EditText = (EditText) findViewById(R.id.EditText_StartTime);
        endTime_EditText = (EditText) findViewById(R.id.EditText_EndTime);
        location_EditText = (EditText) findViewById(R.id.EditText_Location);
        locationNickname_EditText = (EditText) findViewById(R.id.EditText_LocationNickname);
        category_Spinner = (Spinner) findViewById(R.id.Spinner_Category);


        calendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();

        setOnTouchListener(title_EditText);
        setOnTouchListener(description_EditText);
        setOnTouchListener(startTime_EditText);
        setOnTouchListener(endTime_EditText);
        setOnTouchListener(date_EditText);
        setOnTouchListener(locationNickname_EditText);

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

                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000); //Without -1000 the app crashes.
                datePickerDialog.show();
            }
        });

        startTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                updateStartTimeLabel();
            }
        };


        startTime_EditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();

                new TimePickerDialog(
                        AddEventActivity.this,
                        startTimeListener,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true).show();
            }
        });


        endTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                endCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                endCalendar.set(Calendar.MINUTE, minute);

                updateEndTimeLabel();
            }
        };


        endTime_EditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();

                new TimePickerDialog(
                        AddEventActivity.this,
                        endTimeListener,
                        endCalendar.get(Calendar.HOUR_OF_DAY),
                        endCalendar.get(Calendar.MINUTE),
                        true).show();

            }
        });


        location_EditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeHighlightFromEditText(location_EditText);
                openGooglePlaceSearch();
            }
        });


        String[] items = new String[Category.categoriesIndex.length];
        for (int i = 0; i < items.length; i++) {
            items[i] = getString(Category.categoriesIndex[i]);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        category_Spinner.setAdapter(adapter);
    }

    private void openGooglePlaceSearch() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }


    private void setOnTouchListener(final EditText editText) {
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editText.getBackground().clearColorFilter();
                return false;
            }
        });
    }


    private void updateDateLabel() {
        String customFormat = EventData.DATE_FORMAT_STRING;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(customFormat, Locale.ENGLISH);
        date_EditText.setText(simpleDateFormat.format(calendar.getTime()));
    }

    private void updateStartTimeLabel() {
        String customFormat = EventData.TIME_FORMAT_STRING;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(customFormat, Locale.US);
        startTime_EditText.setText(simpleDateFormat.format(calendar.getTime()));
    }

    private void updateEndTimeLabel() {
        String customFormat = EventData.TIME_FORMAT_STRING;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(customFormat, Locale.US);
        endTime_EditText.setText(simpleDateFormat.format(endCalendar.getTime()));
    }

    public void cancel_button(View view)
    {
        Intent intent = new Intent();
        intent.putExtra(IntentExtra_DidAddEvent, false);
        setResult(RESULT_OK, intent);
        finish();
    }

    //TODO: change to listener
    public void addEvent(View v) {
        if (fieldsAreValid()) {
            // Create Event-data and send to Firebase.
            EventData eventData = new EventData(
                    Profile.getCurrentProfile().getId(),
                    title_EditText.getText().toString(),
                    description_EditText.getText().toString(),
                    customLocation,
                    locationNickname_EditText.getText().toString(),
                    category_Spinner.getSelectedItemPosition(),
                    date_EditText.getText().toString(),
                    startTime_EditText.getText().toString(),
                    endTime_EditText.getText().toString()
            );

            FirebaseConnection firebaseConnection = new FirebaseConnection();
            firebaseConnection.AddEvent(eventData);

            Log.i(TAG,eventData.toString());

            Intent intent = new Intent();
            intent.putExtra(IntentExtra_DidAddEvent, true);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private boolean fieldsAreValid() {
        removeHighlightFromEditText(title_EditText);
        removeHighlightFromEditText(description_EditText);
        removeHighlightFromEditText(date_EditText);
        removeHighlightFromEditText(startTime_EditText);
        removeHighlightFromEditText(endTime_EditText);
        removeHighlightFromEditText(location_EditText);
        removeHighlightFromEditText(locationNickname_EditText);

        //Check if input are empty or invalid.


        boolean fieldsOK = true;

        if (fieldIsEmpty(title_EditText)) { fieldsOK = false; }

        if (fieldIsEmpty(description_EditText)) { fieldsOK = false; }

        if (fieldIsEmpty(date_EditText)) { fieldsOK = false; }

        if (fieldIsEmpty(startTime_EditText)) { fieldsOK = false; }

        if (fieldIsEmpty(endTime_EditText)) { fieldsOK = false; }

        if (fieldIsEmpty(location_EditText)) { fieldsOK = false; }

        if (fieldIsEmpty(locationNickname_EditText)) { fieldsOK = false; }

        // Starttime must be earlier than endtime.
        if (timesAreInvalid(startTime_EditText.getText().toString(), endTime_EditText.getText().toString())) {
            highlightInvalidEditText(startTime_EditText);
            highlightInvalidEditText(endTime_EditText);

            fieldsOK = false;
        }

        // If event is today the starttime must be later than current time.
        if (dateConflictsWithTime(startTime_EditText, calendar)) {
            highlightInvalidEditText(date_EditText);
            highlightInvalidEditText(startTime_EditText);

            fieldsOK = false;
        }


        hideKeyboard();
        return fieldsOK;
    }

    private boolean fieldIsEmpty(EditText editText) {
        if (TextUtils.isEmpty(editText.getText().toString())) {
            highlightInvalidEditText(editText);
            return true;
        }
        return false;
    }

    private boolean timesAreInvalid(String time1, String time2) {
        int comp = time1.compareTo(time2);

        if (comp < 0) {
            //T1 is smaller
            return false;
        }
        else if (comp > 0) {
            //T1 is larger
            return true;
        }
        else {
            //T1 is equal to t2
            return true;
        }
    }

    private boolean dateConflictsWithTime(EditText editText, Calendar cal) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(EventData.TIME_FORMAT_STRING);

        if (DateUtils.isToday(cal.getTimeInMillis())) {
            Log.i(TAG, "Is today!");
            if (timesAreInvalid(simpleDateFormat.format(Calendar.getInstance().getTime()), editText.getText().toString())) {
                return true;
            }
        }
        return false;
    }

    private void highlightInvalidEditText(EditText editText) {
        editText.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.DARKEN);
    }

    private void removeHighlightFromEditText(EditText editText) {
        editText.getBackground().clearColorFilter();
    }

    private void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);

                Log.i(TAG, "Place: " + place.getName());
                Log.i(TAG, place.getLatLng().toString() + " " + place.getAddress());

                location_EditText.setText(place.getAddress());
                customLocation.setValues(place.getLatLng().latitude, place.getLatLng().longitude);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }else if(requestCode == 1337){
            if(resultCode == RESULT_OK){
                Float lat = getIntent().getExtras().getFloat("lat");
                Float lon = getIntent().getExtras().getFloat("lat");
                location_EditText.setText(lon.toString());
            }
        }
    }
}
