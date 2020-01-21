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
 * Created by User on 2016-05-16.
 */
public class ConsentActivity extends Activity {  //콘센트 액티비티
    private ImageView consent_image;
    private Switch beacon_switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consent);

        consent_image = (ImageView) findViewById(R.id.consent_image);
        beacon_switch = (Switch) findViewById(R.id.beacon_switch);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Contact.CONSENT_DATA);
        registerReceiver(mRecevier, intentFilter);  //인텐트 필터등록및 브로드캐스트리시버 등록

        try {
            if (SharedPrefsUtils.getStringPreference(getApplicationContext(), Contact.Consent_Becon).equals(Contact.ON)) {
                beacon_switch.setChecked(true);
            } else {
                beacon_switch.setChecked(false);
            }
        } catch (NullPointerException e) {

        }


        beacon_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {  //비콘 모드 스위치 작동 함수
                if (isChecked) {
                    SharedPrefsUtils.setStringPreference(getApplicationContext(), Contact.Consent_Becon, Contact.ON);

                    Intent Consent_Data_intent = new Intent(Contact.CONSENT_DATA);
                    Consent_Data_intent.putExtra("Consent_Becon_flag", Contact.ON);
                    sendBroadcast(Consent_Data_intent);
                } else {
                    SharedPrefsUtils.setStringPreference(getApplicationContext(), Contact.Consent_Becon, Contact.OFF);

                    Intent Consent_Data_intent = new Intent(Contact.CONSENT_DATA);
                    Consent_Data_intent.putExtra("Consent_Becon_flag", Contact.OFF);
                    sendBroadcast(Consent_Data_intent);
                }

            }
        });

        if( SharedPrefsUtils.getStringPreference(getApplicationContext(),"consent_address")==null){
            finish();
            Toast.makeText(getApplicationContext(), "콘센트를 연동한뒤 이용해주세요.", Toast.LENGTH_SHORT).show();
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
            if (intent.getAction().equals(Contact.CONSENT_DATA)) {
                int data = intent.getIntExtra(Contact.data_consent, 0);
                if (data == 1) {
                } else if (data == 2) {
                } else if (data == 3) {
                    finish();
                } else if (data == 4) {
                    consent_image.setBackground(getResources().getDrawable(R.drawable.consent_off));
                } else if (data == 5) {
                    consent_image.setBackground(getResources().getDrawable(R.drawable.consent_on));
                }
            }
        }
    };
}
