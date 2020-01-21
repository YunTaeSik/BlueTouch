package com.bluetouch.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by User on 2016-03-29.
 */
public class BluetoothLeServiceReciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {                  //스마프폰이 부팅되었을때
            Log.e("intentaction","ACTION_BOOT_COMPLETED");
            context.startService(new Intent(context, BluetoothLeService.class));                //서비스시작
            context.startService(new Intent(context, BluetoothLeService_two.class));
            context.startService(new Intent(context, BluetoothLeService_three.class));
            context.startService(new Intent(context, BluetoothLeService_Light.class));
            context.startService(new Intent(context, BluetoothLeService_Consent.class));
            context.startService(new Intent(context, BluetoothLeService_Doorlock.class));
            context.startService(new Intent(context, BluetoothLeService_Valve.class));
        } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {           //패키지가 추가되었을때
            Log.e("intentaction","ACTION_PACKAGE_ADDED");
            context.startService(new Intent(context, BluetoothLeService.class));
            context.startService(new Intent(context, BluetoothLeService_two.class));
            context.startService(new Intent(context, BluetoothLeService_three.class));
            context.startService(new Intent(context, BluetoothLeService_Light.class));
            context.startService(new Intent(context, BluetoothLeService_Consent.class));
            context.startService(new Intent(context, BluetoothLeService_Doorlock.class));
            context.startService(new Intent(context, BluetoothLeService_Valve.class));
        } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {        //패키지가 대체되었을때
            Log.e("intentaction","ACTION_PACKAGE_REPLACED");
            context.startService(new Intent(context, BluetoothLeService.class));
            context.startService(new Intent(context, BluetoothLeService_two.class));
            context.startService(new Intent(context, BluetoothLeService_three.class));
            context.startService(new Intent(context, BluetoothLeService_Light.class));
            context.startService(new Intent(context, BluetoothLeService_Consent.class));
            context.startService(new Intent(context, BluetoothLeService_Doorlock.class));
            context.startService(new Intent(context, BluetoothLeService_Valve.class));
        } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_INSTALL)) {         //패키지가 깔렸을때
            Log.e("intentaction","ACTION_PACKAGE_INSTALL");
            context.startService(new Intent(context, BluetoothLeService.class));
            context.startService(new Intent(context, BluetoothLeService_two.class));
            context.startService(new Intent(context, BluetoothLeService_three.class));
            context.startService(new Intent(context, BluetoothLeService_Light.class));
            context.startService(new Intent(context, BluetoothLeService_Consent.class));
            context.startService(new Intent(context, BluetoothLeService_Doorlock.class));
            context.startService(new Intent(context, BluetoothLeService_Valve.class));
        }
        else if (intent.getAction().equals("Light_connect")) {
            context.startService(new Intent(context, BluetoothLeService_Light.class));
        } else if (intent.getAction().equals("Consent_connect")) {
            Log.e("Consent_connect", "Consent_connect");
            context.startService(new Intent(context, BluetoothLeService_Consent.class));
        } else if (intent.getAction().equals("Valve_connect")) {
            context.startService(new Intent(context, BluetoothLeService_Valve.class));
        } else if (intent.getAction().equals("Doorlock_connect")) {
            context.startService(new Intent(context, BluetoothLeService_Doorlock.class));
        } else if (intent.getAction().equals("Touch_choice")) {
            Log.e("Touch_choice","Touch_choice");
            context.startService(new Intent(context, BluetoothLeService.class));
            context.startService(new Intent(context, BluetoothLeService_two.class));
            context.startService(new Intent(context, BluetoothLeService_three.class));
        }
    }
}
