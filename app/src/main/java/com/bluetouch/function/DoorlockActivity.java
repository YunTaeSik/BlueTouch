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
import com.bluetouch.util.Contact;
import com.bluetouch.util.SharedPrefsUtils;

/**
 * Created by User on 2016-05-18.
 */
public class DoorlockActivity extends Activity{  //도어락 정보 나타내는 액티비티
    private Switch beacon_switch;
    private ImageView doorlock_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doorlock);
        beacon_switch = (Switch) findViewById(R.id.beacon_switch);
        doorlock_image= (ImageView)findViewById(R.id.doorlock_image);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Contact.DOORLOCK_DATA);
        registerReceiver(mRecevier, intentFilter);  //인텐트 필터 등록및 브로드캐스트리시버 등록

        try {  //비콘 스위치 첫 위치 정하기
            if (SharedPrefsUtils.getStringPreference(getApplicationContext(), Contact.Doorlock_Becon).equals(Contact.ON)) {
                beacon_switch.setChecked(true);
            } else {
                beacon_switch.setChecked(false);
            }
        } catch (NullPointerException e) {

        }


        beacon_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {  //비콘 모드 스위치 ON OFF 기능 함수
                if (isChecked) {
                    SharedPrefsUtils.setStringPreference(getApplicationContext(), Contact.Doorlock_Becon, Contact.ON);

                    Intent Consent_Data_intent = new Intent(Contact.DOORLOCK_DATA);
                    Consent_Data_intent.putExtra("DoorLock_Becon_flag", Contact.ON);
                    sendBroadcast(Consent_Data_intent);
                } else {
                    SharedPrefsUtils.setStringPreference(getApplicationContext(), Contact.Doorlock_Becon, Contact.OFF);

                    Intent Consent_Data_intent = new Intent(Contact.DOORLOCK_DATA);
                    Consent_Data_intent.putExtra("DoorLock_Becon_flag", Contact.OFF);
                    sendBroadcast(Consent_Data_intent);
                }

            }
        });

        if( SharedPrefsUtils.getStringPreference(getApplicationContext(),"doorlock_address")==null){
            finish();
            Toast.makeText(getApplicationContext(), "도어락을 연동한뒤 이용해주세요.", Toast.LENGTH_SHORT).show();
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
            if (intent.getAction().equals(Contact.DOORLOCK_DATA)) {
                int data = intent.getIntExtra(Contact.data_doorlock, 0);
                if (data == 1) {
                    doorlock_image.setBackground(getResources().getDrawable(R.drawable.doorlock_open));
                } else if (data == 2) {
                    doorlock_image.setBackground(getResources().getDrawable(R.drawable.doorlock_close));
                } else if (data == 3) {
                    finish();
                } else if (data == 4) {
                } else if (data == 5) {

                }
            }
        }
    };
}
