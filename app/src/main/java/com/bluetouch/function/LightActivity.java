package com.bluetouch.function;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.bluetouch.R;
import com.bluetouch.util.SharedPrefsUtils;

/**
 * Created by User on 2016-04-28.
 */
public class LightActivity extends Activity {   //라이트 액티비티

    private Switch beacon_switch;
    private ImageView light_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("LIGHT_DATA");
        registerReceiver(mRecevier, intentFilter);

        beacon_switch = (Switch) findViewById(R.id.beacon_switch);
        light_image = (ImageView) findViewById(R.id.light_image);
        try {
            if (SharedPrefsUtils.getStringPreference(getApplicationContext(), "Light_Becon").equals("ON")) {
                beacon_switch.setChecked(true);
            } else {
                beacon_switch.setChecked(false);
            }
        } catch (NullPointerException e) {

        }


        beacon_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPrefsUtils.setStringPreference(getApplicationContext(), "Light_Becon", "ON");

                    Intent Light_Data_intent = new Intent("LIGHT_DATA");
                    Light_Data_intent.putExtra("Becon_flag", "ON");
                    sendBroadcast(Light_Data_intent);
                } else {
                    SharedPrefsUtils.setStringPreference(getApplicationContext(), "Light_Becon", "OFF");

                    Intent Light_Data_intent = new Intent("LIGHT_DATA");
                    Light_Data_intent.putExtra("Becon_flag", "OFF");
                    sendBroadcast(Light_Data_intent);
                }

            }
        });

        if( SharedPrefsUtils.getStringPreference(getApplicationContext(),"light_address")==null){
            finish();
            Toast.makeText(getApplicationContext(),"조명을 연동한뒤 이용해주세요.",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mRecevier);
        SharedPrefsUtils.setStringPreference(getApplicationContext(), "Activity_flag", null);
    }

    private BroadcastReceiver mRecevier = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("LIGHT_DATA")) {
                int data = intent.getIntExtra("data_light", 0);
                //  Log.e("LIGHT_DATA", String.valueOf(data));
                if (data == 1) {
                } else if (data == 2) {
                } else if (data == 3) {
                    finish();
                } else if (data == 4) { //켜지면
                    light_image.setBackground(getResources().getDrawable(R.drawable.bulb));
                } else if (data == 5) { //꺼지면
                    light_image.setBackground(getResources().getDrawable(R.drawable.bulb_off));
                }
            }
        }
    };
}
