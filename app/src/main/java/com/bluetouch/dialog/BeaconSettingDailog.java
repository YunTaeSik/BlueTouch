package com.bluetouch.dialog;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.bluetouch.R;
import com.bluetouch.fragment.BlueMianTwoFragment;
import com.bluetouch.util.Contact;
import com.bluetouch.util.SharedPrefsUtils;

/**
 * Created by User on 2016-05-14.
 */
public class BeaconSettingDailog extends Activity implements View.OnClickListener { //비콘 모드 온오프 정하는 다이얼로그

    private String getTag;
    private Button beacon_on;
    private Button beacon_off;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.activity_beacon_setting_dailog);
        setFinishOnTouchOutside(false);

        beacon_on = (Button) findViewById(R.id.beacon_on);
        beacon_off = (Button) findViewById(R.id.beacon_off);
        beacon_on.setOnClickListener(this);
        beacon_off.setOnClickListener(this);

        Intent intent = getIntent();
        getTag = intent.getStringExtra("Setting_flag");

    }

    @Override
    protected void onResume() {
        super.onResume(); //태그 정보 구분후 해당 브로드캐스트리시버에 정보전달
        if (getTag.equals(BlueMianTwoFragment.LIGHT)) {
            Intent intent = new Intent();
            intent.setAction("Light_connect");
            sendBroadcast(intent);
        } else if (getTag.equals(BlueMianTwoFragment.CONSENT)) {
            Intent intent = new Intent();
            intent.setAction("Consent_connect");
            sendBroadcast(intent);
        } else if (getTag.equals(BlueMianTwoFragment.VALVE)) {
            Intent intent = new Intent();
            intent.setAction("Valve_connect");
            sendBroadcast(intent);
        } else if (getTag.equals(BlueMianTwoFragment.DOORLOCK)) {
            Intent intent = new Intent();
            intent.setAction("Doorlock_connect");
            sendBroadcast(intent);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) { //버튼 클릭시  조건에 맞는 태그별로 브로드캐스트리시버에 정보전달
            case R.id.beacon_on:
                if (getTag.equals(BlueMianTwoFragment.LIGHT)) {
                    SharedPrefsUtils.setStringPreference(getApplicationContext(), "Light_Becon", "ON");
                    Intent Light_Data_intent = new Intent("LIGHT_DATA");
                    Light_Data_intent.putExtra("Becon_flag", "ON");
                    sendBroadcast(Light_Data_intent);
                } else if (getTag.equals(BlueMianTwoFragment.CONSENT)) {
                    SharedPrefsUtils.setStringPreference(getApplicationContext(), Contact.Consent_Becon, Contact.ON);
                    Intent Consent_Data_intent = new Intent(Contact.CONSENT_DATA);
                    Consent_Data_intent.putExtra("Consent_Becon_flag", Contact.ON);
                    sendBroadcast(Consent_Data_intent);
                } else if (getTag.equals(BlueMianTwoFragment.VALVE)) {
                    SharedPrefsUtils.setStringPreference(getApplicationContext(), Contact.Valve_Becon, Contact.ON);
                    Intent Consent_Data_intent = new Intent(Contact.VALVE_DATA);
                    Consent_Data_intent.putExtra("Valve_Becon_flag", Contact.ON);
                    sendBroadcast(Consent_Data_intent);
                } else if (getTag.equals(BlueMianTwoFragment.DOORLOCK)) {
                    SharedPrefsUtils.setStringPreference(getApplicationContext(), Contact.Doorlock_Becon, Contact.ON);
                    Intent Consent_Data_intent = new Intent(Contact.DOORLOCK_DATA);
                    Consent_Data_intent.putExtra("DoorLock_Becon_flag", Contact.ON);
                    sendBroadcast(Consent_Data_intent);
                }
                finish();
                break;
            case R.id.beacon_off:
                if (getTag.equals(BlueMianTwoFragment.LIGHT)) {
                    SharedPrefsUtils.setStringPreference(getApplicationContext(), "Light_Becon", "OFF");
                    Intent Light_Data_intent = new Intent("LIGHT_DATA");
                    Light_Data_intent.putExtra("Becon_flag", "OFF");
                    sendBroadcast(Light_Data_intent);

                } else if (getTag.equals(BlueMianTwoFragment.CONSENT)) {
                    SharedPrefsUtils.setStringPreference(getApplicationContext(), Contact.Consent_Becon, Contact.OFF);
                    Intent Consent_Data_intent = new Intent(Contact.CONSENT_DATA);
                    Consent_Data_intent.putExtra("Consent_Becon_flag", Contact.OFF);
                    sendBroadcast(Consent_Data_intent);
                } else if (getTag.equals(BlueMianTwoFragment.VALVE)) {
                    SharedPrefsUtils.setStringPreference(getApplicationContext(), Contact.Valve_Becon, Contact.OFF);
                    Intent Consent_Data_intent = new Intent(Contact.VALVE_DATA);
                    Consent_Data_intent.putExtra("Valve_Becon_flag", Contact.OFF);
                    sendBroadcast(Consent_Data_intent);
                } else if (getTag.equals(BlueMianTwoFragment.DOORLOCK)) {
                    SharedPrefsUtils.setStringPreference(getApplicationContext(), Contact.Doorlock_Becon, Contact.OFF);
                    Intent Consent_Data_intent = new Intent(Contact.DOORLOCK_DATA);
                    Consent_Data_intent.putExtra("DoorLock_Becon_flag", Contact.OFF);
                    sendBroadcast(Consent_Data_intent);
                }

                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }
}
