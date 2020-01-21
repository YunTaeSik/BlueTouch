package com.bluetouch.function;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;

import com.bluetouch.fragment.BlueMainOneFragment;
import com.bluetouch.fragment.BlueMianTwoFragment;
import com.bluetouch.util.Contact;
import com.bluetouch.util.SharedPrefsUtils;

/**
 * Created by User on 2016-04-05.
 */
public class OneFunction extends Activity {  //기능 작동 함수
    private Context context;

    public OneFunction() {    //생성자
    }

    public void SetFuntion(String function, Context context, int data) {  // function 구분후 해당 기능 작동 하게하는 함수
        if (function != null) {
            if (function.equals(BlueMainOneFragment.CAMERA)) {
                final Intent cameraintent = new Intent(context, CameraActivity.class);
                cameraintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(cameraintent);
                SharedPrefsUtils.setStringPreference(context, "Activity_flag", "CAMERA");
            } else if (function.equals(BlueMainOneFragment.CALL)) {
                String call_number = SharedPrefsUtils.getStringPreference(context, "call_number");
                Intent callintent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + call_number));
                callintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                context.startActivity(callintent);
            } else if (function.equals(BlueMainOneFragment.MESSAGE)) {
                String message_number = SharedPrefsUtils.getStringPreference(context, "message_number");
                String message_string = SharedPrefsUtils.getStringPreference(context, "message_string");
                SmsManager smsManager = SmsManager.getDefault();
                if (message_number != null) {
                    smsManager.sendTextMessage(message_number, null, message_string, null, null);
                }
            } else if (function.equals(BlueMainOneFragment.NAVIGATION)) {
                String navigation_start = SharedPrefsUtils.getStringPreference(context, "navigation_start");
                String navigation_finish = SharedPrefsUtils.getStringPreference(context, "navigation_finish");
                Intent naintent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?f=d&saddr=" + navigation_start + "&daddr=" + navigation_finish + "&hl=ko"));
                naintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(naintent);

            } else if (function.equals(BlueMainOneFragment.FAKECALL)) {
                Intent fakeintent = new Intent(context, FakeCallActivity.class);
                fakeintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(fakeintent);
                SharedPrefsUtils.setStringPreference(context, "Activity_flag", "FAKECALL");
            } else if (function.equals(BlueMainOneFragment.WEATHER)) {
                Intent weatherintent = new Intent(context, WeatherActivity.class);
                weatherintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(weatherintent);
                SharedPrefsUtils.setStringPreference(context, "Activity_flag", "WEATHER");
            } else if (function.equals(BlueMainOneFragment.ALARM)) {
                Intent alarmintent = new Intent(context, AlarmActivity.class);
                alarmintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(alarmintent);
                SharedPrefsUtils.setStringPreference(context, "Activity_flag", "ALARM");
            } else if (function.equals(BlueMianTwoFragment.LIGHT)) {
                final Intent lightintent = new Intent(context, LightActivity.class);
                lightintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(lightintent);
                Intent Light_Data_intent = new Intent("LIGHT_DATA");
                Light_Data_intent.putExtra("data_light", 4);
                context.sendBroadcast(Light_Data_intent);
                SharedPrefsUtils.setStringPreference(context, "Activity_flag", "LIGHT");
            } else if (function.equals(BlueMianTwoFragment.CONSENT)) {
                final Intent consentintent = new Intent(context, ConsentActivity.class);
                consentintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(consentintent);
                Intent Consent_Data_intent = new Intent(Contact.CONSENT_DATA);
                Consent_Data_intent.putExtra(Contact.data_consent, 5);
                context.sendBroadcast(Consent_Data_intent);
                SharedPrefsUtils.setStringPreference(context, "Activity_flag", "CONSENT");
            } else if (function.equals(BlueMianTwoFragment.VALVE)) {
                final Intent consentintent = new Intent(context, ValveActivity.class);
                consentintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(consentintent);
                Intent Consent_Data_intent = new Intent(Contact.VALVE_DATA);
                Consent_Data_intent.putExtra(Contact.data_valve, 4);
                context.sendBroadcast(Consent_Data_intent);
                SharedPrefsUtils.setStringPreference(context, "Activity_flag", "VALVE");
            } else if (function.equals(BlueMianTwoFragment.DOORLOCK)) {
                final Intent consentintent = new Intent(context, DoorlockActivity.class);
                consentintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(consentintent);
                Intent Consent_Data_intent = new Intent(Contact.DOORLOCK_DATA);
                Consent_Data_intent.putExtra(Contact.data_doorlock, 1);
                context.sendBroadcast(Consent_Data_intent);
                SharedPrefsUtils.setStringPreference(context, "Activity_flag", "DOORLOCK");
            }
        }
    }
}
