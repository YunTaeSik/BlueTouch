package com.bluetouch.setting;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

import com.bluetouch.R;
import com.bluetouch.util.SharedPrefsUtils;


/**
 * Created by User on 2016-04-08.
 */
public class CallSettingActivity extends Activity {
    private EditText call_input_edit;
    private String call_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_setting);

        call_input_edit = (EditText)findViewById(R.id.call_input_edit);

        if(SharedPrefsUtils.getStringPreference(getApplicationContext(), "call_number") !=null){
            call_input_edit.setText(SharedPrefsUtils.getStringPreference(getApplicationContext(), "call_number"));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        call_number = call_input_edit.getText().toString();

        SharedPrefsUtils.setStringPreference(getApplicationContext(),"call_number",call_number);
    }
}
