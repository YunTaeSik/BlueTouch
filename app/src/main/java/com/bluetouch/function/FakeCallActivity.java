package com.bluetouch.function;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;

import com.bluetouch.R;
import com.bluetouch.util.SharedPrefsUtils;

/**
 * Created by User on 2016-04-08.
 */
public class FakeCallActivity extends Activity {   //페이크콜 액티비티
    private MediaPlayer player;
    private Vibrator mVibe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fakecall);

        player = MediaPlayer.create(this,
                Settings.System.DEFAULT_RINGTONE_URI);
        mVibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        Notification(player, mVibe);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("FAKE_DATA");
        registerReceiver(mRecevier, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.stop();
        mVibe.cancel();
        unregisterReceiver(mRecevier);
        SharedPrefsUtils.setStringPreference(getApplicationContext(), "Activity_flag", null);
    }

    private void Notification(MediaPlayer player, Vibrator mVibe) {  //진동 설정과 알람 설정
        long[] time = {2000, 200};
        mVibe.vibrate(time, 0);
        player.start();
    }


    private BroadcastReceiver mRecevier = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("FAKE_DATA")) {
                int data = intent.getIntExtra("data_fake", 0);
                Log.e("FAKE_DATA", String.valueOf(data));
                if (data == 1) {
                } else if (data == 2) {
                } else if (data == 3) {
                    finish();
                }
            }
        }
    };


}
