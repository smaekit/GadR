package com.marcusjakobsson.gadr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CreateStatus extends AppCompatActivity {

    public static final int REQUEST_CODE_DidAddStatus = 2;
    public static final String IntentExtra_DidAddStatus = "didAddStatus";
    public static final String IntentExtra_UserStatus = "userStatus";

    EditText status_editText;
    Button publishStatusBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_status);

        status_editText = (EditText)findViewById(R.id.status_EditText);
        publishStatusBtn = (Button)findViewById(R.id.publish_button);

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

        Intent intent = new Intent();
        intent.putExtra(IntentExtra_DidAddStatus, true);
        intent.putExtra(IntentExtra_UserStatus, userStatus);
        setResult(RESULT_OK, intent);
        finish();
    }
}
