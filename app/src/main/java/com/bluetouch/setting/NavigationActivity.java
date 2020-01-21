package com.bluetouch.setting;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

import com.bluetouch.R;
import com.bluetouch.util.SharedPrefsUtils;

/**
 * Created by User on 2016-04-08.
 */
public class NavigationActivity extends Activity {

    private EditText navigation_input_start;
    private EditText navigation_input_finish;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_setting);

        navigation_input_start = (EditText)findViewById(R.id.navigation_input_start);
        navigation_input_finish = (EditText)findViewById(R.id.navigation_input_finish);

        if(SharedPrefsUtils.getStringPreference(getApplicationContext(),"navigation_start") != null){
            navigation_input_start.setText(SharedPrefsUtils.getStringPreference(getApplicationContext(),"navigation_start"));
        }
        if(SharedPrefsUtils.getStringPreference(getApplicationContext(),"navigation_finish") !=null){
            navigation_input_finish.setText(SharedPrefsUtils.getStringPreference(getApplicationContext(),"navigation_finish"));
        }



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SharedPrefsUtils.setStringPreference(getApplicationContext(), "navigation_start", navigation_input_start.getText().toString());
        SharedPrefsUtils.setStringPreference(getApplicationContext(), "navigation_finish", navigation_input_finish.getText().toString());
    }
}
