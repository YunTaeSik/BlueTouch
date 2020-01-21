package com.bluetouch.function;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
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
public class ValveActivity extends Activity {  //벨브 액티비티
    private Switch beacon_switch;
    private ImageView valve_close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valve);
        beacon_switch = (Switch) findViewById(R.id.beacon_switch);
        valve_close = (ImageView)findViewById(R.id.valve_close);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Contact.VALVE_DATA);
        registerReceiver(mRecevier, intentFilter);  //인텐트 필터 등록및 브로드캐스트 리시버 등록

        try {  //처음 비콘 모드 스위치 정보 읽고 나타내는 함수
            if (SharedPrefsUtils.getStringPreference(getApplicationContext(), Contact.Valve_Becon).equals(Contact.ON)) {
                beacon_switch.setChecked(true);
            } else {
                beacon_switch.setChecked(false);
            }
        } catch (NullPointerException e) {

        }


        beacon_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {  //비콘 모드 스위치 on off 정의 함수
                if (isChecked) {
                    SharedPrefsUtils.setStringPreference(getApplicationContext(), Contact.Valve_Becon, Contact.ON);

                    Intent Consent_Data_intent = new Intent(Contact.VALVE_DATA);
                    Consent_Data_intent.putExtra("Valve_Becon_flag", Contact.ON);
                    sendBroadcast(Consent_Data_intent);
                } else {
                    SharedPrefsUtils.setStringPreference(getApplicationContext(), Contact.Valve_Becon, Contact.OFF);

                    Intent Consent_Data_intent = new Intent(Contact.VALVE_DATA);
                    Consent_Data_intent.putExtra("Valve_Becon_flag", Contact.OFF);
                    sendBroadcast(Consent_Data_intent);
                }

            }
        });

        if( SharedPrefsUtils.getStringPreference(getApplicationContext(),"valve_address")==null){
            finish();
            Toast.makeText(getApplicationContext(), "벨브를 연동한뒤 이용해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mRecevier);  //브로드캐스트리시버 삭제
        SharedPrefsUtils.setStringPreference(getApplicationContext(), "Activity_flag", null); //액티비티 플래그 정보 null 처리
    }

    private BroadcastReceiver mRecevier = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Contact.VALVE_DATA)) {
                int data = intent.getIntExtra(Contact.data_valve, 0);
                if (data == 1) {
                } else if (data == 2) {
                } else if (data == 3) {
                    finish();
                } else if (data == 4) {
                    valve_close.setVisibility(View.GONE);
                } else if (data == 5) {
                    valve_close.setVisibility(View.VISIBLE);
                }
            }
        }
    };
}
