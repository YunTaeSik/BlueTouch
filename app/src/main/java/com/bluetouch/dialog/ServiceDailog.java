package com.bluetouch.dialog;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;

import com.bluetouch.R;

/**
 * Created by User on 2016-05-16.
 */
public class ServiceDailog extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.activity_service_dailog);
        setFinishOnTouchOutside(false);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Service_finish");
        registerReceiver(mRecevier, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mRecevier);
    }

    private BroadcastReceiver mRecevier = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("Service_finish")) {
                int data = intent.getIntExtra("finish", 0);
                if (data == 1) {
                    finish();
                }
            }
        }
    };
}
