package com.bluetouch.setting;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

import com.bluetouch.R;
import com.bluetouch.util.SharedPrefsUtils;

/**
 * Created by User on 2016-04-08.
 */
public class MessageSettingActivity extends Activity {

    private EditText message_input_number;
    private EditText message_input_string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_setting);

        message_input_number = (EditText)findViewById(R.id.message_input_number);
        message_input_string =(EditText)findViewById(R.id.message_input_string);

        if(SharedPrefsUtils.getStringPreference(getApplication(),"message_number")!=null){
            message_input_number.setText(SharedPrefsUtils.getStringPreference(getApplication(),"message_number"));
        }

        if(SharedPrefsUtils.getStringPreference(getApplication(),"message_string")!=null){
            message_input_string.setText(SharedPrefsUtils.getStringPreference(getApplication(),"message_string"));
        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        SharedPrefsUtils.setStringPreference(getApplicationContext(), "message_number", message_input_number.getText().toString());
        SharedPrefsUtils.setStringPreference(getApplicationContext(), "message_string", message_input_string.getText().toString());
    }
}
