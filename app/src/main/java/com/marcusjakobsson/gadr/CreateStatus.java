package com.marcusjakobsson.gadr;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CreateStatus extends AppCompatActivity {

    public static final int REQUEST_CODE_DidAddStatus = 2;
    public static final String IntentExtra_DidAddStatus = "didAddStatus";
    public static final String IntentExtra_UserStatus = "userStatus";

    private EditText status_editText;
    private Button publishStatusBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_status);

        status_editText = findViewById(R.id.status_EditText);
        publishStatusBtn = findViewById(R.id.publish_button);
    }

    public void cancel_button(View view)
    {
        Intent intent = new Intent();
        intent.putExtra(IntentExtra_DidAddStatus, false);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void publish_button(View view)
    {
        FirebaseConnection firebaseConnection = new FirebaseConnection();
        String userStatus = status_editText.getText().toString();
        firebaseConnection.AddStatus(userStatus);

        updateWidget(userStatus);

        //Code for the app
        Intent intent = new Intent();
        intent.putExtra(IntentExtra_DidAddStatus, true);
        intent.putExtra(IntentExtra_UserStatus, userStatus);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void updateWidget(String userStatus)
    {
        //Start Code for widget
        SharedPreferences sharedPreferences = this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(IntentExtra_UserStatus, userStatus).apply();

        Intent intent2 = new Intent(this, GadRWidget.class);
        intent2.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        // Code will fire the onUpdate() in widget:
        int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), GadRWidget.class));
        intent2.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent2);
        //End Code for widget
    }
}
